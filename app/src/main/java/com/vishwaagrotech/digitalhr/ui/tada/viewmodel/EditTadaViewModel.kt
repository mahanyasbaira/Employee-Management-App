package com.vishwaagrotech.digitalhr.ui.tada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Attachment
import com.vishwaagrotech.digitalhr.model.Tada
import com.vishwaagrotech.digitalhr.repository.TadaRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.Exception
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class EditTadaViewModel
@Inject
constructor(val repository: TadaRepository) : ViewModel() {

    private val _tada = MutableStateFlow<EventHandler<Tada>>(EventHandler.Empty)
     val tada = _tada.asStateFlow();

    private val _attachmentList = MutableStateFlow<ArrayList<Attachment>>(arrayListOf())
    private val _oldAttachmentList = MutableStateFlow<ArrayList<Attachment>>(arrayListOf())

    private val _createTada = MutableStateFlow<EventHandler<Boolean>>(EventHandler.Empty)
    private val _errorMessage = MutableStateFlow<EventHandler<String>>(EventHandler.Empty)

    val attachmentList = _attachmentList.asStateFlow()
    val oldAttachmentList = _oldAttachmentList.asStateFlow()

    val createTada = _createTada.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()

    fun addAttachment(path: String) {

        val currentList = _attachmentList.value.toMutableList()
        currentList.add(Attachment(Random().nextInt(), path,""))


        _attachmentList.value = ArrayList(currentList)
    }

    fun removeAttachment(attachment: Attachment) {
        val currentList = _attachmentList.value.toMutableList()
        val value = currentList.find {
            it.id == attachment.id
        }

        currentList.remove(value)

        _attachmentList.value = ArrayList(currentList)
    }

    fun submitTada(id:String,title: String, description: String, expenses: String) {
        val filesList = mutableListOf<MultipartBody.Part>()
        for (attachment in attachmentList.value) {
            val file1 = File(attachment.url)
            val filePart1 =
                MultipartBody.Part.createFormData(
                    "attachments[]",
                    file1.name,
                    file1.asRequestBody()
                )
            filesList.add(filePart1)
        }

        _createTada.value = EventHandler.Loading
        viewModelScope.launch {
            val idReq = id.toRequestBody("text/plain".toMediaTypeOrNull())
            val titleReq = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionReq = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val expensesReq = expenses.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = try {
                repository.editTadaDetailResponse(idReq,titleReq, descriptionReq, expensesReq, filesList)
            } catch (e: Exception) {
                e.printStackTrace()
                _createTada.value = EventHandler.Failure("Something went wrong")
                return@launch
            }

            if (response.isSuccessful) {
                if (response.body()!!.status_code == 200) {
                    _createTada.value = EventHandler.Success(true)
                } else {
                    _createTada.value = EventHandler.Success(false)
                }
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )

                _createTada.value = EventHandler.Failure(responseError.message)
            }
        }

    }

    fun onTadaAttachmentDelete(attachment: Attachment) {
        _errorMessage.value = EventHandler.Loading
        viewModelScope.launch {
            val response = try {
                repository.deleteTadaAttachmentResponse(attachment.id.toString())
            } catch (e: Exception) {
                e.printStackTrace();
                _errorMessage.value = EventHandler.Failure("Something went wrong")
                return@launch
            }

            if (response.isSuccessful) {
                val currentList = _oldAttachmentList.value.toMutableList()
                val value = currentList.find {
                    it.id == attachment.id
                }

                currentList.remove(value)

                _oldAttachmentList.value = ArrayList(currentList)
                _errorMessage.value = EventHandler.Success("Attachment has been deleted")
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )

                _errorMessage.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun onTadaAttachmentDetail(id: String) {
        _tada.value = EventHandler.Loading
        viewModelScope.launch {
            val response = try {
                repository.getTadaDetailResponse(id)
            } catch (e: Exception) {
                e.printStackTrace();
                _tada.value = EventHandler.Failure("Something went wrong.")
                return@launch
            }

            if (response.isSuccessful) {
                val value = response.body()!!.data;
                var attachments: ArrayList<Attachment> = ArrayList()

                for (attachment in value.attachments.image) {
                    attachments.add(Attachment(attachment.id, attachment.url,""))
                }

                for (attachment in value.attachments.file) {
                    attachments.add(Attachment(attachment.id, attachment.url,""))
                }

                val rTada = Tada(
                    value.employee,
                    value.id,
                    value.remark,
                    value.status,
                    value.submitted_date,
                    value.title,
                    value.total_expense,
                    value.description,
                    value.verified_by,
                    attachments
                )

                _oldAttachmentList.value = attachments
                _tada.value = EventHandler.Success(rTada);

            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )

                _tada.value = EventHandler.Failure(responseError.message)
            }
        }
    }
}