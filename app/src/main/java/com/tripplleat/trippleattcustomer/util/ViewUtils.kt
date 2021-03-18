package com.tripplleat.trippleattcustomer.util


import android.app.Dialog
import android.content.Context
import android.widget.ProgressBar
import android.widget.Toast
import com.tripplleat.trippleattcustomer.R
import kotlinx.android.synthetic.main.dialog_design.*

fun Context.toast(message : String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun Context.showDialog(message: String):Dialog{
    val dialog= Dialog(this)
    dialog.setContentView(R.layout.dialog_design)
    dialog.setCancelable(false)
    dialog.text.text=message
    return dialog

}


