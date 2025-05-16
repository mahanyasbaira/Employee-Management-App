package com.vishwaagrotech.digitalhr.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentProfileBinding
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.UserProfileViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.GetProperImageRotation
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.vishwaagrotech.digitalhr.utils.makeToast
import com.google.android.gms.common.util.IOUtils
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URLEncoder


@AndroidEntryPoint
class ProfileFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener {
    lateinit var binding: FragmentProfileBinding

    private val model: UserProfileViewModel by viewModels()

    private lateinit var filePermissionRequest: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.handler = this

        binding.viewmodel = model
        binding.lifecycleOwner = this

        requestFilePermission()

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchLocalValue()
        observeProfile()
        observeProfileChange()

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)

        onRefresh()

    }


    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Profile"
        binding.toolbar.toolbar.setTitleTextColor(Color.WHITE)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        } else if (item.itemId == R.id.action_update_profile) {
            updateProfilePage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateProfilePage() {
        val bundle = bundleOf(
            "fullname" to binding.textName.text.toString(),
            "email" to binding.textEmail.text.toString(),
            "address" to binding.textAddress.text.toString(),
            "phone" to binding.textPhone.text.toString(),
            "gender" to binding.textGender.text.toString(),
            "dob" to binding.textDob.text.toString(),
        )
        findNavController().navigate(R.id.action_profileFragment2_to_updateProfileFragment, bundle)
    }

    private fun fetchLocalValue() {
        model.localUserDetail.observe(viewLifecycleOwner) {
            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.placeholder)
                .into(binding.imageProfile)
            binding.textName.text = it.name
            binding.textUsername.text = it.username
        }

        model.getStoreValue()
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.userResponse.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            binding.refreshPage.setRefreshing(false)
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            binding.refreshPage.setRefreshing(false)

                            if (it.errorText != null) {
                                binding.linearOtherDetail.visibility = View.GONE
                            }
                        }
                    }
                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.showDialog(context, false)
                            binding.refreshPage.setRefreshing(true)
                        }
                    }
                    is EventHandler.Success -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            binding.refreshPage.setRefreshing(false)

                            if (it.result != null) {
                                binding.linearOtherDetail.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRefresh() {
        model.getProfileResponse()
    }

    fun changeProfilePicture() {
        filePermissionRequest.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        )
    }

    private fun checkFilePermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            filePermissionRequest.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
            return
        }
    }


    private fun requestFilePermission() {
        filePermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
                    val i = Intent()
                    i.type = "image/*"
                    i.action = Intent.ACTION_GET_CONTENT

                    resultLauncher.launch(i)
                }
                else -> {
                    checkFilePermission()
                }
            }
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data

                val imageURI = data?.data
                val path = getRealPathFromURI(requireContext(), imageURI!!)

                if (path != null) {
                    var rotatedImageFile = GetProperImageRotation.getRotatedImageFile(File(path),context)
                    model.updateProfilePicture(rotatedImageFile!!)
                }
            }
        }

    @SuppressLint("NewApi")
    fun getRealPathFromURI(context: Context, uri: Uri): String {
        val isKitKat: Boolean =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) { //main if start

            // DocumentProvider
            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(context, uri)
            } else if (isExternalStorageDocument(uri)) {// ExternalStorageProvider

                val docId: String = DocumentsContract.getDocumentId(uri)
                val split: List<String> = docId.split(":")
                val type: String = split[0]             // This is for checking Main Memory
                if ("primary".equals(type, ignoreCase = true)) {
                    if (split.size > 1) {
                        return context.getExternalFilesDir(null).toString() + "/" + split[1]
                    } else {
                        return context.getExternalFilesDir(null).toString() + "/"
                    }
                    // This is for checking SD Card
                } else {
                    return "storage" + "/" + docId.replace(":", "/")
                }
            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider           \
                val parcelFileDescriptor =
                    context.contentResolver.openFileDescriptor(
                        uri,
                        "r",
                        null
                    )
                parcelFileDescriptor?.let {
                    val inputStream = FileInputStream(
                        parcelFileDescriptor.fileDescriptor
                    )
                    val file = File(
                        context.cacheDir,
                        context.contentResolver.getFileName(uri)
                    )
                    val outputStream = FileOutputStream(file)
                    IOUtils.copyStream(inputStream, outputStream)
                    return file.path
                }
            } else if (isMediaDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split: List<String> = docId.split(":")
                return copyFileToInternalStorage(context, uri, "app name")

            }
        }//main if end
        else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // MediaStore (and general)
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment!!
            else copyFileToInternalStorage(
                context,
                uri,
                "your app name"
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // File
            return uri.path!!
        }
        return null!!
    }

    fun ContentResolver.getFileName(fileUri: Uri): String {
        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(
                OpenableColumns.DISPLAY_NAME
            )
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return URLEncoder.encode(name, "utf-8")
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents".equals(uri.authority)
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage".equals(uri.authority)
                || "com.google.android.apps.docs.storage.legacy".equals(uri.authority)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents"
            .equals(uri.authority)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents"
            .equals(uri.authority)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content"
            .equals(uri.authority)
    }

    private fun getDriveFilePath(context: Context, uri: Uri): String {
        val returnCursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)

        val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = (returnCursor.getString(nameIndex))
        val file = File(
            context.cacheDir,
            URLEncoder.encode(name, "utf-8")
        )
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            val maxBufferSize: Int = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()
            //int bufferSize = 1024;
            val bufferSize: Int =
                Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            inputStream.use { inputStream: InputStream ->
                outputStream.use { fileOut ->
                    while (true) {
                        val length = inputStream.read(buffers)
                        if (length <= 0)
                            break
                        fileOut.write(buffers, 0, length)
                    }
                    fileOut.flush()
                    fileOut.close()
                }
            }

            Log.e("File Size", "Size " + file.length())
            inputStream.close()
            Log.e("File Path", "Path " + file.path)
            Log.e("File Size", "Size " + file.length())
        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }
        return file.path
    }

    private fun copyFileToInternalStorage(mContext: Context, uri: Uri, newDirName: String): String {
        val returnCursor: Cursor? = mContext.contentResolver.query(
            uri,
            arrayOf(
                OpenableColumns.DISPLAY_NAME,
                OpenableColumns.SIZE
            ),
            null,
            null,
            null
        )
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val output: File
        if (newDirName != "") {
            val dir = File(
                mContext.filesDir.toString()
                        + "/" + newDirName
            )
            if (!dir.exists()) {
                dir.mkdir()
            }
            output = File(
                mContext.filesDir.toString()
                        + "/" + newDirName + "/"
                        + URLEncoder.encode(name, "utf-8")
            )
        } else {
            output = File(
                mContext.filesDir.toString()
                        + "/" + URLEncoder.encode(name, "utf-8")
            )
        }
        try {
            val inputStream: InputStream? =
                mContext.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(output)
            var read = 0
            val bufferSize = 1024
            val buffers = ByteArray(bufferSize)
            while (inputStream!!.read(buffers)
                    .also { read = it } != -1
            ) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return output.path
    }

    private fun observeProfileChange() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.profileUpdate.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            requireActivity().makeToast(it.errorText)
                        }
                    }
                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.showDialog(context, false)
                        }
                    }
                    is EventHandler.Success -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            requireActivity().makeToast(it.result.message)
                        }
                    }
                }
            }
        }
    }


}