package com.lion.a066ex_animalmanager.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.a066ex_animalmanager.MainActivity
import com.lion.a066ex_animalmanager.R
import com.lion.a066ex_animalmanager.dao.AnimalDatabase
import com.lion.a066ex_animalmanager.databinding.FragmentShowBinding
import com.lion.a066ex_animalmanager.repository.AnimalRepository
import com.lion.a066ex_animalmanager.util.FragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ShowFragment : Fragment() {

    lateinit var fragmentShowBinding: FragmentShowBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentShowBinding = FragmentShowBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        settingToolbar()

        settingTextView()

        return fragmentShowBinding.root
    }

    // 툴바 설정 메서드
    fun settingToolbar(){
        fragmentShowBinding.apply {
            // 타이틀
            toolbarShow.title = "동물 정보 보기"
            // 네비게이션
            toolbarShow.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarShow.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.SHOW_FRAGMENT)
            }
            // 메뉴
            toolbarShow.inflateMenu(R.menu.show_toolbar_menu)
            toolbarShow.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.show_toolbar_menu_edit -> {
                        // 동물 번호를 담아준다
                        val dataBundle = Bundle()
                        dataBundle.putInt("animalIdx", arguments?.getInt("animalIdx")!!)
                        // ModifyFragment로 이동한다
                        mainActivity.replaceFragment(FragmentName.MODIFY_FRAGMENT, true, dataBundle)
                    }
                    R.id.show_toolbar_menu_delete -> {
                        deleteAnimalInfo()
                    }
                }
                true
            }
        }
    }

    // TextView에 들어갈 값을 설정하는 메서드
    fun settingTextView(){
        fragmentShowBinding.apply {
            if(arguments != null) {
                // TextView를 초기화 한다
                textViewShowType.text = " "
                textViewShowName.text = " "
                textViewShowAge.text = " "
                textViewShowGender.text = " "
                textViewShowWeight.text = " "

                // 동물 번호를 추출한다.
                val animalIdx = arguments?.getInt("animalIdx")
                // 동물 데이터를 가져와 출력한다
                CoroutineScope(Dispatchers.Main).launch {
                    val work1 = async(Dispatchers.IO){
                        AnimalRepository.selectAnimalInfoByAnimalIdx(mainActivity, animalIdx!!)
                    }
                    val animalViewModel = work1.await()

                    textViewShowType.text = "종류 : ${animalViewModel.animalType.str}"
                    textViewShowName.text = "이름 : ${animalViewModel.animalName}"
                    textViewShowAge.text = "나이 : ${animalViewModel.animalAge.toString()}"
                    textViewShowGender.text = "성별 : ${animalViewModel.animalGender.str}"
                    textViewShowWeight.text = "몸무게 : ${animalViewModel.animalWeight.toString()}"
                }

//                // 순서값을 가져온다
//                val position = arguments?.getInt("position")!!
//                // TextView에 값을 넣어준다
//
//                textViewShowType.text = mainActivity.dataList[position].animalType
//                textViewShowName.text = mainActivity.dataList[position].animalName
//                textViewShowAge.text = mainActivity.dataList[position].animalAge.toString()
//                textViewShowGender.text = mainActivity.dataList[position].animalGender
//                textViewShowWeight.text = mainActivity.dataList[position].animalWeight.toString()
            }
        }
    }

    // 삭제 처리 메서드
    fun deleteAnimalInfo() {
        // 다이얼로그를 띄워준다
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
        materialAlertDialogBuilder.setTitle("삭제")
        materialAlertDialogBuilder.setMessage("삭제를 할 경우 복원이 불가능합니다")
        materialAlertDialogBuilder.setNegativeButton("취소",null)
        materialAlertDialogBuilder.setPositiveButton("삭제"){ dialogInterface: DialogInterface, i:Int ->
            val animalDatabase = AnimalDatabase.getInstance(mainActivity)
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO){
                    val animalIdx = arguments?.getInt("animalIdx")
                    AnimalRepository.deleteAnimalInfoByAnimalIdx(mainActivity, animalIdx!!)
                }
                work1.join()
                mainActivity.removeFragment(FragmentName.SHOW_FRAGMENT)
            }
        }
        materialAlertDialogBuilder.show()
    }


}