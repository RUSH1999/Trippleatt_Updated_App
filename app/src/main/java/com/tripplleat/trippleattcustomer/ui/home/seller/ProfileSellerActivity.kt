package com.tripplleat.trippleattcustomer.ui.home.seller

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.squareup.picasso.Picasso
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.ActivityProfileSellerBinding
import com.tripplleat.trippleattcustomer.databinding.FragmentHomeBinding
import com.tripplleat.trippleattcustomer.modal.businessData
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal
import com.tripplleat.trippleattcustomer.ui.home.seller.listeners.picUploadListener
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast
import kotlinx.android.synthetic.main.activity_profile_seller.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ProfileSellerActivity : Fragment() ,KodeinAware,picUploadListener{
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: HomeViewModelFactory by instance()
    private var viewModel: HomeViewModal?=null
    private val PICK_IMAGE1_REQUEST = 71
    private val PICK_IMAGE2_REQUEST = 72
    private val PICK_IMAGE3_REQUEST = 73
    private var imagebutton:ImageView?=null
    private var binding : ActivityProfileSellerBinding ?=null
    private var dialoge:Dialog?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding  = DataBindingUtil.inflate(inflater,R.layout.activity_profile_seller,container,false)
      viewModel  = ViewModelProvider(this@ProfileSellerActivity,factory).get(HomeViewModal::class.java)
        viewModel?.checkingFilledDetails()
        binding?.viewmodel = viewModel
        binding?.lifecycleOwner = this
binding?.addPictureButton= View.OnClickListener {
    when(it.tag.toString()){
        "1"->uploadPicture(PICK_IMAGE1_REQUEST)
            "2"->uploadPicture(PICK_IMAGE2_REQUEST)
        "3"->uploadPicture(PICK_IMAGE3_REQUEST)
    }
}

        viewModel?.pictureUploadListener=this
dialoge=requireContext().showDialog("loading...")

        viewModel?.busidata?.observe(viewLifecycleOwner, Observer {
           checkForInputProvided(it)

        })
        return binding?.root
    }


//Fetch all data from firestore
    private fun checkForInputProvided(it: businessData) {

    if (!it.picture1.isEmpty()) {
        binding?.pic1?.visibility = View.VISIBLE
        Picasso.get().load(it.picture1).into(binding?.pic1)
        Picasso.get().load(it.picture1).into(binding?.pic1)
    }
    if (!it.picture2.isEmpty()) {
        binding?.pic2?.visibility = View.VISIBLE
        Picasso.get().load(it.picture2).into(binding?.pic2)
    }
    if (!it.picture3.isEmpty()) {
        binding?.pic3?.visibility = View.VISIBLE
        Picasso.get().load(it.picture3).into(binding?.pic3)
    }
    if (!it.businame.isEmpty()){
        binding?.shopname?.setText(it.businame)
    }
    if (!it.emailid.isEmpty()){
        binding?.textEmail?.setText(it.emailid)
    }
    if (!it.category.isEmpty()){
        binding?.category?.setText(it.category)
    }
    if (!it.website.isEmpty()){
        binding?.website?.setText(it.website)
    }
    if (!it.fulladdress.isEmpty()){
        binding?.fulladdress?.setText(it.fulladdress)
    }
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){

            PICK_IMAGE1_REQUEST->
            if (resultCode == Activity.RESULT_OK) {
                imagebutton=binding?.pic1
                imagebutton?.visibility=View.VISIBLE
                Picasso.get().load(data?.data).fit().into(imagebutton!!)
                viewModel?.uploadPicture("picture1",data?.data!!)
            }
            PICK_IMAGE2_REQUEST->
                if (resultCode == Activity.RESULT_OK) {
                    imagebutton=binding?.pic2
                    imagebutton?.visibility=View.VISIBLE
                    Picasso.get().load(data?.data).fit().into(imagebutton!!)
                    viewModel?.uploadPicture("picture2",data?.data!!)
                }
            PICK_IMAGE3_REQUEST->
                if (resultCode == Activity.RESULT_OK) {
                    imagebutton=binding?.pic3
                    imagebutton?.visibility=View.VISIBLE
                    Picasso.get().load(data?.data).fit().into(imagebutton!!)
                    viewModel?.uploadPicture("picture3",data?.data!!)
                }

        }
        if(requestCode == PICK_IMAGE1_REQUEST || requestCode == PICK_IMAGE2_REQUEST ||requestCode == PICK_IMAGE3_REQUEST && resultCode == Activity.RESULT_OK)
        {

            if (data != null) {


            };

        }

    }
    fun uploadPicture(pickRequest:Int){
        val intent=Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,pickRequest)
    }

    override fun onStarted() {
dialoge?.show()
    }

    override fun onFailed(e: String) {
dialoge?.dismiss()
        requireContext().toast(e)
    }

    override fun onSuccess() {
dialoge?.dismiss()
    }
}
