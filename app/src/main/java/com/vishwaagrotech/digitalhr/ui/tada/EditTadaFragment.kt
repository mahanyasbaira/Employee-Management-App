package com.vishwaagrotech.digitalhr.ui.tada

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentEditTadaBinding
import com.vishwaagrotech.digitalhr.handler.AttachmentHandler
import com.vishwaagrotech.digitalhr.model.Attachment
import com.vishwaagrotech.digitalhr.ui.tada.adapter.LocalAttachmentListAdapter
import com.vishwaagrotech.digitalhr.ui.tada.adapter.ServerAttachmentListAdapter
import com.vishwaagrotech.digitalhr.ui.tada.viewmodel.EditTadaViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.google.android.gms.common.util.IOUtils
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
class EditTadaFragment : Fragment(), AttachmentHandler {
    lateinit var binding: FragmentEditTadaBinding

    val model: EditTadaViewModel by viewModels()

    lateinit var attachmentListAdapter: LocalAttachmentListAdapter
    lateinit var oldAttachmentListAdapter: ServerAttachmentListAdapter

    private var attachments: ArrayList<Attachment> = ArrayList()
    private var oldAttachments: ArrayList<Attachment> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_tada, container, false)
        binding.handler = this
        binding.lifecycleOwner = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.onTadaAttachmentDetail(arguments!!.getString("tadaId", "0"))
        makeLocalAttachment()
        observeAttachments()
        observeTadaDetail()
        observerError()
        observeTada()
    }

    private fun makeLocalAttachment() {
        attachmentListAdapter = LocalAttachmentListAdapter(attachments, this)
        binding.recyclerViewAttachmentList.apply {
            adapter = attachmentListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        oldAttachmentListAdapter = ServerAttachmentListAdapter(oldAttachments, this)
        binding.recyclerViewOldAttachmentList.apply {
            adapter = oldAttachmentListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun observeAttachments() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            withContext(Dispatchers.Main) {
                model.attachmentList.collect {
                    attachments.clear()
                    attachments.addAll(it)
                    attachmentListAdapter.notifyDataSetChanged()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            withContext(Dispatchers.Main) {
                model.oldAttachmentList.collect {
                    oldAttachments.clear()
                    oldAttachments.addAll(it)
                    oldAttachmentListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun observeTada() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.createTada.collectLatest {
                when (it) {
                    EventHandler.Empty -> {
                        LoadingUtils.hideDialog()
                    }

                    is EventHandler.Failure -> {
                        LoadingUtils.hideDialog()
                        Toast.makeText(requireContext(), it.errorText, Toast.LENGTH_SHORT).show()
                    }

                    EventHandler.Loading -> {
                        LoadingUtils.showDialog(requireContext(), false)
                    }

                    is EventHandler.Success -> {
                        LoadingUtils.hideDialog()
                        Toast.makeText(
                            requireContext(),
                            "Tada has been edited",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun observerError() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.errorMessage.collectLatest {
                when (it) {
                    EventHandler.Empty -> {
                        LoadingUtils.hideDialog()
                    }

                    is EventHandler.Failure -> {
                        LoadingUtils.hideDialog()
                        Toast.makeText(requireContext(), it.errorText, Toast.LENGTH_SHORT).show()
                    }

                    EventHandler.Loading -> {
                        LoadingUtils.showDialog(requireContext(), false)
                    }

                    is EventHandler.Success -> {
                        LoadingUtils.hideDialog()
                        Toast.makeText(
                            requireContext(),
                            it.result,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun observeTadaDetail() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.tada.collectLatest {
                when (it) {
                    EventHandler.Empty -> {
                        LoadingUtils.hideDialog()
                    }

                    is EventHandler.Failure -> {
                        LoadingUtils.hideDialog()
                        Toast.makeText(requireContext(), it.errorText, Toast.LENGTH_SHORT).show()
                    }

                    EventHandler.Loading -> {
                        LoadingUtils.showDialog(requireContext(), false)
                    }

                    is EventHandler.Success -> {
                        LoadingUtils.hideDialog()
                        binding.editTitle.setText(it.result.title)
                        binding.editDescription.setText(it.result.description)
                        binding.editExpenses.setText(it.result.total_expense)
                    }
                }
            }
        }
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Edit TADA"
        binding.toolbar.toolbar.setTitleTextColor(Color.WHITE)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun submitClicked() {
        val title = binding.editTitle.text.toString()
        val description = binding.editDescription.text.toString()
        val expenses = binding.editExpenses.text.toString()

        if (title.isEmpty()) {
            binding.editTitle.error = "Field is empty"
            return
        }

        if (description.isEmpty()) {
            binding.editDescription.error = "Field is empty"
            return
        }

        if (expenses.isEmpty()) {
            binding.editExpenses.error = "Field is empty"
            return
        }

        model.submitTada(arguments!!.getString("tadaId")!!, title, description, expenses)
    }

    private val selectImagesActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                //If multiple image selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0

                    for (i in 0 until count) {
                        val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                        val path = getRealPathFromURI(requireContext(), imageUri!!)

                        if (path != null) {
                            model.addAttachment(path)
                        }
                    }
                }
                //If single image selected
                else if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    val path = getRealPathFromURI(requireContext(), imageUri!!)

                    if (path != null) {
                        model.addAttachment(path)
                    }
                }
            }
        }


    fun selectAttachmentFiles() {
        val intent = Intent(ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "*/*"
        selectImagesActivityResult.launch(intent)
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

    override fun onAttachmentClicked(attachment: Attachment) {
        model.removeAttachment(attachment)
    }

    override fun onRemoveAttachmentClicked(attachment: Attachment) {
        model.onTadaAttachmentDelete(attachment)
    }

    override fun onLoadAttachmentClicked(attachment: Attachment) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(attachment.url))
        startActivity(browserIntent)
    }
}