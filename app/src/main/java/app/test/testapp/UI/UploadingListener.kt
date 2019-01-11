package app.test.testapp.UI

interface UploadingListener {

    fun onError()

    fun onSuccess()

    fun onStartUploading()

    fun setCurrentProgressing(progress: Int)
}