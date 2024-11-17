package com.lion.a066ex_animalmanager.viewmodel

import com.lion.a066ex_animalmanager.util.AnimalGender
import com.lion.a066ex_animalmanager.util.AnimalType

data class AnimalViewModel (
    // 나중에 idx 꼭 넣어주기
    var animalIdx : Int,
    var animalType : AnimalType,
    var animalName : String,
    var animalAge : Int,
    var animalGender : AnimalGender,
    var animalWeight : Int
)