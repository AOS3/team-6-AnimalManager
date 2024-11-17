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
import com.lion.a066ex_animalmanager.databinding.FragmentModifyBinding
import com.lion.a066ex_animalmanager.repository.AnimalRepository
import com.lion.a066ex_animalmanager.util.AnimalGender
import com.lion.a066ex_animalmanager.util.AnimalType
import com.lion.a066ex_animalmanager.util.FragmentName
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ModifyFragment : Fragment() {

    lateinit var fragmentModifyBinding: FragmentModifyBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentModifyBinding = FragmentModifyBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        settingToolbar()

        settingInput()

        return fragmentModifyBinding.root
    }

    // Toolbar 설정
    fun settingToolbar(){
        fragmentModifyBinding.apply {
            // 타이틀
            materialToolbarModify.title = "동물 정보 수정"
            // 네비게이션
            materialToolbarModify.setNavigationIcon(R.drawable.arrow_back_24px)
            materialToolbarModify.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)
            }
            materialToolbarModify.inflateMenu(R.menu.modify_toolbar_menu)
            materialToolbarModify.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.modify_toolbar_menu_done -> {
                        modifyDone()
                    }
                }
                true
            }
        }
    }

    // 입력 요소들 초기 설정
    fun settingInput(){
        fragmentModifyBinding.apply {
            // 동물 번호를 가져온다.
            val animalIdx = arguments?.getInt("animalIdx")!!
            // 동물 데이터를 가져온다.
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    AnimalRepository.selectAnimalInfoByAnimalIdx(mainActivity, animalIdx)
                }
                val animalViewModel = work1.await()

                // 종류
                when(animalViewModel.animalType){
                    AnimalType.ANIMAL_TYPE_DOG -> {
                        animalButtonGroupModify.check(R.id.button_dog_modify)
                    }
                    AnimalType.ANIMAL_TYPE_CAT -> {
                        animalButtonGroupModify.check(R.id.button_cat_modify)
                    }
                    AnimalType.ANIMAL_TYPE_PARROT -> {
                        animalButtonGroupModify.check(R.id.button_parrot_modify)
                    }
                }
                // 이름
                textFieldNameModify.editText?.setText(animalViewModel.animalName)
                // 나이
                textFieldAgeModify.editText?.setText(animalViewModel.animalAge.toString())
                // 성별
                when(animalViewModel.animalGender){
                    AnimalGender.ANIMAL_GENDER_FEMALE -> {
                        animalGenderGroupModify.check(R.id.buttonFemale_modify)
                    }
                    AnimalGender.ANIMAL_GENDER_MALE -> {
                        animalGenderGroupModify.check(R.id.buttonMale_modify)
                    }
                }
                // 몸무게
                silderAnimalWeightModify.value = animalViewModel.animalWeight.toFloat()

            }
        }
    }

    // 수정 처리 메서드
    fun modifyDone(){
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(mainActivity)
        materialAlertDialogBuilder.setTitle("수정")
        materialAlertDialogBuilder.setMessage("이전 데이터로 복원할 수 없습니다")
        materialAlertDialogBuilder.setNeutralButton("취소", null)
        materialAlertDialogBuilder.setPositiveButton("수정"){dialogInterface: DialogInterface, i: Int ->
            // 수정할 데이터
            val animalIdx = arguments?.getInt("animalIdx")!!
            val animalType = when(fragmentModifyBinding.animalButtonGroupModify.checkedButtonId){
                R.id.button_dog_modify -> AnimalType.ANIMAL_TYPE_DOG
                R.id.button_cat_modify -> AnimalType.ANIMAL_TYPE_CAT
                else -> AnimalType.ANIMAL_TYPE_PARROT
            }
            val animalName = fragmentModifyBinding.textFieldNameModify.editText?.text.toString()
            val animalAge = fragmentModifyBinding.textFieldAgeModify.editText?.text.toString().toInt()
            val animalGender = when(fragmentModifyBinding.animalGenderGroupModify.checkedButtonId){
                R.id.buttonFemale_modify -> AnimalGender.ANIMAL_GENDER_FEMALE
                else -> AnimalGender.ANIMAL_GENDER_MALE
            }
            val animalWeight = fragmentModifyBinding.silderAnimalWeightModify.value.toInt()

            val animalViewModel = AnimalViewModel(animalIdx, animalType, animalName, animalAge, animalGender, animalWeight)

            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    AnimalRepository.updateAnimalInfo(mainActivity,animalViewModel)
                }
                work1.join()
                mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)
            }
        }
        materialAlertDialogBuilder.show()
    }

}