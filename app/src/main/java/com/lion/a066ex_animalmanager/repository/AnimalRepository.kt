package com.lion.a066ex_animalmanager.repository

import android.content.Context
import com.lion.a066ex_animalmanager.dao.AnimalDatabase
import com.lion.a066ex_animalmanager.util.AnimalGender
import com.lion.a066ex_animalmanager.util.AnimalType
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel
import com.lion.a066ex_animalmanager.vo.AnimalVO

class AnimalRepository {

    companion object{

        // 동물 정보를 저장하는 메서드
        fun insertAnimalInfo(context:Context,animalViewModel: AnimalViewModel){
            // 데이터베이스 객체를 가져온다
            val animalDatabase = AnimalDatabase.getInstance(context)
            // ViewModel에 있는 데이터를 VO에 담아준다
            val animalType = animalViewModel.animalType.number
            val animalName = animalViewModel.animalName
            val animalAge = animalViewModel.animalAge
            val animalGender = animalViewModel.animalGender.number
            val animalWeight = animalViewModel.animalWeight

            val animalVO = AnimalVO(animalType = animalType, animalName = animalName, animalAge = animalAge, animalGender = animalGender, animalWeight = animalWeight)

            // 저장한다
            animalDatabase?.animalDAO()?.insertAnimalData(animalVO)
        }

        // 동물 정보 전체를 가져오는 메서드
        fun selectAnimalInfoAll(context: Context) : MutableList<AnimalViewModel>{
            // 데이터베이스 객체
            val animalDatabase = AnimalDatabase.getInstance(context)
            // 동물 데이터 전체를 가져온다
            val animalVoList = animalDatabase?.animalDAO()?.selectAnimalDataAll()
            // 동물 데이터를 담을 리스트
            val animalViewModelList = mutableListOf<AnimalViewModel>()
            // 동물 수 만큼 반복한다.
            animalVoList?.forEach {
                // 동물 데이터를 추출한다
                val animalType = when(it.animalType) {
                    AnimalType.ANIMAL_TYPE_DOG.number -> AnimalType.ANIMAL_TYPE_DOG
                    AnimalType.ANIMAL_TYPE_CAT.number -> AnimalType.ANIMAL_TYPE_CAT
                    else -> AnimalType.ANIMAL_TYPE_PARROT
                }
                val animalName = it.animalName
                val animalAge = it.animalAge

                val animalGender = when(it.animalGender){
                    AnimalGender.ANIMAL_GENDER_FEMALE.number -> AnimalGender.ANIMAL_GENDER_FEMALE
                    else -> AnimalGender.ANIMAL_GENDER_MALE
                }
                val animalWeight = it.animalWeight
                val animalIdx = it.animalIdx
                // 객체에 담는다
                val animalViewModel = AnimalViewModel(animalIdx,animalType,animalName,animalAge,animalGender,animalWeight)
                // 리스트에 담는다
                animalViewModelList.add(animalViewModel)
            }
            return animalViewModelList
        }

        fun selectAnimalInfoByAnimalIdx(context: Context, animalIdx:Int) : AnimalViewModel{
            val animalDatabase = AnimalDatabase.getInstance(context)
            // 동물 하나의 정보를 가져온다
            val animalVO = animalDatabase?.animalDAO()?.selectAnimalDataByAnimalIdx(animalIdx)
            // 동물 객체에 담는다
            val animalType = when(animalVO?.animalType) {
                AnimalType.ANIMAL_TYPE_DOG.number -> AnimalType.ANIMAL_TYPE_DOG
                AnimalType.ANIMAL_TYPE_CAT.number -> AnimalType.ANIMAL_TYPE_CAT
                else -> AnimalType.ANIMAL_TYPE_PARROT

            }
            val animalName = animalVO?.animalName
            val animalAge = animalVO?.animalAge
            val animalGender = when(animalVO?.animalGender){
                AnimalGender.ANIMAL_GENDER_FEMALE.number -> AnimalGender.ANIMAL_GENDER_FEMALE
                else -> AnimalGender.ANIMAL_GENDER_MALE
            }
            val animalWeight = animalVO?.animalWeight

            val animalViewModel = AnimalViewModel(animalIdx, animalType, animalName!!, animalAge!!, animalGender, animalWeight!!)

            return animalViewModel
        }

        // 동물 정보 삭제
        fun deleteAnimalInfoByAnimalIdx(context: Context, animalIdx: Int){
            val animalDatabase = AnimalDatabase.getInstance(context)
            // 삭제할 동물 번호를 담고 있을 객체를 생성한다
            val animalVO = AnimalVO(animalIdx = animalIdx)
            // 삭제한다
            animalDatabase?.animalDAO()?.deleteAnimalData(animalVO)
        }

        // 동물 정보를 수정하는 메서드
        fun updateAnimalInfo(context: Context, animalViewModel: AnimalViewModel) {
            val animalDatabase = AnimalDatabase.getInstance(context)
            // VO에 객체에 담아준다.
            val animalIdx = animalViewModel.animalIdx
            val animalType = animalViewModel.animalType.number
            val animalName = animalViewModel.animalName
            val animalAge = animalViewModel.animalAge
            val animalGender = animalViewModel.animalGender.number
            val animalWeight = animalViewModel.animalWeight
            val animalVO = AnimalVO(animalIdx, animalType, animalName, animalAge, animalGender, animalWeight)
            // 수정한다.
            animalDatabase?.animalDAO()?.updateAnimalData(animalVO)
        }

        fun selectAnimalInfoByType(context: Context, type: Int): MutableList<AnimalViewModel> {
            val animalDatabase = AnimalDatabase.getInstance(context)
            val animalVoList = animalDatabase?.animalDAO()?.selectAnimalDataByType(type)
            val animalViewModelList = mutableListOf<AnimalViewModel>()

            animalVoList?.forEach {
                val animalType = when (it.animalType) {
                    AnimalType.ANIMAL_TYPE_DOG.number -> AnimalType.ANIMAL_TYPE_DOG
                    AnimalType.ANIMAL_TYPE_CAT.number -> AnimalType.ANIMAL_TYPE_CAT
                    else -> AnimalType.ANIMAL_TYPE_PARROT
                }
                val animalGender = when (it.animalGender) {
                    AnimalGender.ANIMAL_GENDER_FEMALE.number -> AnimalGender.ANIMAL_GENDER_FEMALE
                    else -> AnimalGender.ANIMAL_GENDER_MALE
                }
                animalViewModelList.add(
                    AnimalViewModel(
                        animalIdx = it.animalIdx,
                        animalType = animalType,
                        animalName = it.animalName,
                        animalAge = it.animalAge,
                        animalGender = animalGender,
                        animalWeight = it.animalWeight
                    )
                )
            }
            return animalViewModelList
        }
    }
}