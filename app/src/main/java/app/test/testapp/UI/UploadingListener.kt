package app.test.testapp.UI

interface UploadingListener {

    fun showToast(message: String)

    fun showProgressDialog(title:String)

    fun closeProgressDialog()

    fun setCurrentProgressing(progress: Int)
}