package com.lion.a066ex_animalmanager.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.lion.a066ex_animalmanager.MainActivity
import com.lion.a066ex_animalmanager.R
import com.lion.a066ex_animalmanager.databinding.FragmentInputBinding
import com.lion.a066ex_animalmanager.repository.AnimalRepository
import com.lion.a066ex_animalmanager.util.AnimalGender
import com.lion.a066ex_animalmanager.util.AnimalType
import com.lion.a066ex_animalmanager.util.FragmentName
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class InputFragment : Fragment() {

    lateinit var fragmentInputBinding: FragmentInputBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentInputBinding = FragmentInputBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        settingToolbar()

        return fragmentInputBinding.root
    }

    // 툴바를 세팅하는 메서드
    fun settingToolbar() {
        fragmentInputBinding.apply {
            // 타이틀
            materialToolbarInput.title = "동물 정보 입력"
            // 좌측 네비게이션 버튼
            materialToolbarInput.setNavigationIcon(R.drawable.arrow_back_24px)
            materialToolbarInput.setNavigationOnClickListener {
                mainActivity.supportFragmentManager.popBackStack()
            }

            materialToolbarInput.inflateMenu(R.menu.toolbar_input_add)
            materialToolbarInput.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.input_toolbar_menu_done -> {
                        // 사용자가 입력한 데이터를 가져온다
                        // 동물 종류
                        val animalType = when(animalButtonGroup.checkedButtonId){
                            // 강아지
                            R.id.button_dog -> AnimalType.ANIMAL_TYPE_DOG
                            // 고양이
                            R.id.button_cat -> AnimalType.ANIMAL_TYPE_CAT
                            // 앵무새
                            else -> AnimalType.ANIMAL_TYPE_PARROT
                        }
                        // 이름
                        val animalName = fragmentInputBinding.textFieldName.editText?.text.toString()
                        // 나이
                        val animalAge = fragmentInputBinding.textFieldAge.editText?.text.toString().toInt()
                        // 성별
                        val animalGender = when(animalGenderGroup.checkedButtonId){
                            // 암컷
                            R.id.buttonFemale -> AnimalGender.ANIMAL_GENDER_FEMALE
                            // 수컷
                            else -> AnimalGender.ANIMAL_GENDER_MALE
                        }
                        // 몸무게
                        val animalWeight = fragmentInputBinding.silderAnimalWeight.value.toInt()

                        // 객체에 담는다
                        val animalViewModel = AnimalViewModel(0,animalType, animalName, animalAge, animalGender, animalWeight)

                        // 데이터를 저장하는 메서드를 코루틴으로 운영한다.
                        CoroutineScope(Dispatchers.Main).launch {
                            // 저장작업이 끝날때까지 대기한다
                            val work1 = async(Dispatchers.IO){
                                // 저장한다
                                AnimalRepository.insertAnimalInfo(mainActivity,animalViewModel)
                            }
                            work1.join()
                            // 저장한다.
                            mainActivity.removeFragment(FragmentName.INPUT_FRAGMENT)
                        }
                    }
                }
                true
            }
        }
    }


}