package app.test.testapp.UI

interface UploadingListener {

    fun onError(e: Exception)

    fun onSuccess()

    fun onStartUploading()

    fun setCurrentProgressing(progress: Int)
}