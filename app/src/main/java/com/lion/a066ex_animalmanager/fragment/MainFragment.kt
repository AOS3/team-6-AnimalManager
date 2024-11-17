package com.lion.a066ex_animalmanager.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.a066ex_animalmanager.MainActivity
import com.lion.a066ex_animalmanager.R
import com.lion.a066ex_animalmanager.databinding.ActivityMainBinding
import com.lion.a066ex_animalmanager.databinding.FragmentMainBinding
import com.lion.a066ex_animalmanager.databinding.RowMainBinding
import com.lion.a066ex_animalmanager.repository.AnimalRepository
import com.lion.a066ex_animalmanager.util.AnimalType
import com.lion.a066ex_animalmanager.util.FragmentName
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    lateinit var fragmentMainBinding: FragmentMainBinding
    lateinit var mainActivity: MainActivity

    var animalList = mutableListOf<AnimalViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentMainBinding = FragmentMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        settingToolbar()

        settingRecyclerViewMain()

        settingFAB()

        refreshRecyclerViewMain()

        return fragmentMainBinding.root
    }

    fun settingToolbar() {
        fragmentMainBinding.materialToolbarMain.apply {
            val filter = arguments?.getString("filter")
            title = when(filter) {
                "dog" -> "강아지 정보 목록"
                "cat" -> "고양이 정보 목록"
                "parrot" -> "앵무새 정보 목록"
                else -> "전체 목록"
            }
            // 좌측 네비게이션 뷰 버튼
            setNavigationIcon(R.drawable.menu_24px)
            // 좌측 리스너
            setNavigationOnClickListener {
                mainActivity.activityMainBinding.drawerLayoutMain.open()
            }
        }
    }
    // fab 버튼 리스너
    fun settingFAB() {
        fragmentMainBinding.apply {
            fabMain.setOnClickListener{
                mainActivity.replaceFragment(FragmentName.INPUT_FRAGMENT, true, null)
            }
        }
    }

    // RecyclerView 구성
    fun settingRecyclerViewMain() {

        fragmentMainBinding.apply {
            // 어댑터 세팅
            recyclerViewMain.adapter = RecyclerViewMainAdapter()
            // 보여주는 형식
            recyclerViewMain.layoutManager = LinearLayoutManager(activity)
            // 구분선
            val deco = MaterialDividerItemDecoration(requireActivity(), MaterialDividerItemDecoration.VERTICAL)
            recyclerViewMain.addItemDecoration(deco)
        }
    }

    // 동물 정보를 가져와 RecyclerView를 갱신하는 메서드
    fun refreshRecyclerViewMain(){
        val filter = arguments?.getString("filter")

        // 동물 정보를 가져온다
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO){
                when(filter) {
                    "dog" -> AnimalRepository.selectAnimalInfoByType(mainActivity, AnimalType.ANIMAL_TYPE_DOG.number)
                    "cat" -> AnimalRepository.selectAnimalInfoByType(mainActivity, AnimalType.ANIMAL_TYPE_CAT.number)
                    "parrot" -> AnimalRepository.selectAnimalInfoByType(mainActivity, AnimalType.ANIMAL_TYPE_PARROT.number)
                    else -> AnimalRepository.selectAnimalInfoAll(mainActivity)
                }
            }
            animalList = work1.await()
            // RecyclerView를 갱신한다
            fragmentMainBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
        }
    }

    // RecyclerView 어댑터
    inner class RecyclerViewMainAdapter() : RecyclerView.Adapter<RecyclerViewMainAdapter.MainViewHolder>() {

        // ViewHolder
        inner class MainViewHolder(var rowMainBinding: RowMainBinding) : RecyclerView.ViewHolder(rowMainBinding.root), OnClickListener {
            override fun onClick(v: View?) {
                // 사용자가 누른 동물의 동물 번호를 담아준다
                val dataBundle = Bundle()
                dataBundle.putInt("animalIdx",animalList[adapterPosition].animalIdx)
                // ShowFragment로 이동한다
                mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT,true, dataBundle)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val rowMainViewHolder = RowMainBinding.inflate(layoutInflater, parent, false)
            val mainViewHolder = MainViewHolder(rowMainViewHolder)

            // 리스너를 설정해준다
            rowMainViewHolder.root.setOnClickListener(mainViewHolder)

            return mainViewHolder
        }

        override fun getItemCount(): Int {
            return animalList.size
        }

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            holder.rowMainBinding.textViewMainRow.text = animalList[position].animalName
        }
    }

}