/**
 *
 * 프로젝트 설명
 *
 * @ 프로젝트 구조는 MVVM 아키텍처와 안드로이드 Jetpack 컴포넌트가 사용되었습니다.
 *   사용된 외부 라이브러리는 gradle 모듈 파일 끝에 설명되어 있습니다.
 *
 * @ UI는 하나의 컨테이너 액티비티만 포함하고 네비게이션 컴포넌트를 이용해 필요한 프래그먼트를 보여주는 방식입니다.
 *   (네비게이션 그래프는 nav_graph.xml 에 정의되어 있습니다.)
 *   프래그먼트는 UI 조작을 위한 코드만 포함하고, 뷰모델에는 데이터에 접근하는 코드와 비즈니스 로직만 포함되어 있습니다.
 *
 * @ 사용된 DB는 로컬, 원격 두 가지입니다.
 *   Category 클래스는 SQLite 로컬 DB 에 저장되고 회원정보, Action 클래스는 Google Firebase 원격 DB 에 저장됩니다.
 *
 *
 * 패키지 설명 (알파벳 순서)
 *
 * @ api : Pixabay 사이트의 사진 검색 API 를 사용하기 위한 클래스들 (Retrofit 이용)
 *
 * @ data : 앱에서 사용하는 모든 자료형 및 DB 관련 클래스들
 *
 *  - action : 앱의 기록 기능을 통해 유저가 기록한 하나의 기록에 대한 정보가 담긴 Action 클래스와 DB 관련 클래스들
 *  - category : 카테고리 하나에 관한 정보가 담긴 Category 클래스 (ex. 대중교통 이용)와 DB 관련 클래스들
 *  - detailedaction : DetailedAction 클래스 (Action 정보에, 연계된 Category 정보까지 추가된 클래스) 와 DB 관련 클래스들
 *  - photo : Pixabay 사진 하나에 대한 정보가 담긴 PixabayPhoto 클래스와 DB 관련 클래스들
 *
 * @ di : Dagger-Hilt 의존성 주입을 위한 패키지. AppModule 클래스 하나만 사용합니다.
 *
 * @ ui : 화면을 정의하는 클래스들 (프래그먼트들과 각각의 뷰모델들)
 *  - add : 기록 추가를 위한 화면들
 *      - category : 카테고리를 선택하는 화면
 *      - add : 카테고리를 선택한 후 기록을 최종 추가하는 화면을 정의함
 *      - photo : 사진 검색 및 선택을 위한 화면
 *  - authentication : 회원 인정을 위한 화면들
 *      - signin : 로그인을 위한 화면
 *      - signup : 회원가입을 위한 화면
 *  - calendar : 달력 화면
 *  - home : 로그인 후 보이는 홈 화면
 *  - statistic : 통계 화면
 *  - today : 조회 화면. 달력 화면에서 날짜 클릭 시 날짜를 인수를 전달하는 방식으로 재활용함
 *  - trend : 현황 화면
 *
 * @ util : 위의 패키지에 포함되지 않는, 편의성 및 코드 단축을 위한 클래스들
 *  - AuthFragment : Firebase 의 로그인, 로그아웃 이벤트 처리를 위한 추상 클래스. 로그인 후의 프래그먼트들이 상속받음
 *  - Utils : 문자열 획득, 시간을 문자열로 바꾸는 등의 static 메소드 제공
 *  - OnTextChangedListener : 에딧텍스트의 리스너인 TextWatcher 가 너무 길어서 짧게 정의한 리스너 클래스
 *
 * @ GreenWorldApplication 클래스 : 직접 사용하지 않으나 Dagger-Hilt 의존성 주입을 위해 필요함
 *
 */

package com.davidjo.greenworld.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.davidjo.greenworld.R;
import com.davidjo.greenworld.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 액션바 설정하기
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 네비게이션 호스트 설정하기
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}

