package com.lion.a066ex_animalmanager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.transition.MaterialSharedAxis
import com.lion.a066ex_animalmanager.databinding.ActivityMainBinding
import com.lion.a066ex_animalmanager.fragment.InputFragment
import com.lion.a066ex_animalmanager.fragment.MainFragment
import com.lion.a066ex_animalmanager.fragment.ModifyFragment
import com.lion.a066ex_animalmanager.fragment.ShowFragment
import com.lion.a066ex_animalmanager.util.FragmentName
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingNavigationView()

        // 첫 화면을 설정한다
        replaceFragment(FragmentName.MAIN_FRAGMENT, false, null)
    }

    fun settingNavigationView() {
        activityMainBinding.navigationViewMain.apply {
            // 제일 처음 선택된 상태로 이동
            setCheckedItem(R.id.navigation_show_all)

            setNavigationItemSelectedListener {
                if(it.isCheckable == true) {
                    it.isChecked = true
                }

                when(it.itemId) {
                    R.id.navigation_show_all -> {
                        replaceFragment(FragmentName.MAIN_FRAGMENT, false, null)
                    }
                    R.id.navigation_dog -> {
                        replaceFragment(FragmentName.MAIN_FRAGMENT, false, Bundle().apply {
                            putString("filter", "dog")
                        })
                    }
                    R.id.navigation_cat -> {
                        replaceFragment(FragmentName.MAIN_FRAGMENT, false, Bundle().apply {
                            putString("filter", "cat")
                        })
                    }
                    R.id.navigation_parrot -> {
                        replaceFragment(FragmentName.MAIN_FRAGMENT, false, Bundle().apply {
                            putString("filter", "parrot")
                        })
                    }
                }
                activityMainBinding.drawerLayoutMain.close()
                true
            }
        }
    }

    // 프래그먼트를 교체하는 함수
    fun replaceFragment(fragmentName: FragmentName, isAddToBackStack:Boolean, dataBundle: Bundle?){
        // 프래그먼트 객체
        val newFragment = when(fragmentName){
            // 첫 화면
            FragmentName.MAIN_FRAGMENT -> MainFragment()
            // 입력 화면
            FragmentName.INPUT_FRAGMENT -> InputFragment()
            // 출력 화면
            FragmentName.SHOW_FRAGMENT -> ShowFragment()
            // 수정 화면
            FragmentName.MODIFY_FRAGMENT -> ModifyFragment()

        }

        // bundle 객체가 null이 아니라면
        if(dataBundle != null){
            newFragment.arguments = dataBundle
        }

        // 프래그먼트 교체
        supportFragmentManager.commit {

            newFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
            newFragment.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
            newFragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
            newFragment.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

            replace(R.id.fragmentContainerViewMain, newFragment)
            // 이건 나중에 데이터베이스 추가할 때 넣기
            // replace(R.id.containerMain, newFragment)
            if(isAddToBackStack){
                addToBackStack(fragmentName.str)
            }
        }

    }

    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeFragment(fragmentName: FragmentName){
        supportFragmentManager.popBackStack(fragmentName.str, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}