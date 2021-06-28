package com.holy.interiortalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class CategoryFragment : Fragment(), View.OnClickListener {

    // 프래그먼트 리스너 인터페이스
    interface BoardFragmentListener {
        fun onCategoryViewClicked(viewId: Int)
    }

    // 리스너 인스턴스
    private var listener: BoardFragmentListener? = null

    // 리스너 설정
    fun setFragmentListener(listener: BoardFragmentListener) {
        this.listener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        // 카드뷰에 클릭 리스너를 설정한다
        val livingRoomCard = view.findViewById<CardView>(R.id.card_living_room)
        val bedroomCard = view.findViewById<CardView>(R.id.card_bedroom)
        val kitchenCard = view.findViewById<CardView>(R.id.card_kitchen)
        val bathroomCard = view.findViewById<CardView>(R.id.card_bathroom)
        val rankingCard = view.findViewById<CardView>(R.id.card_ranking)
        livingRoomCard.setOnClickListener(this)
        bedroomCard.setOnClickListener(this)
        kitchenCard.setOnClickListener(this)
        bathroomCard.setOnClickListener(this)
        rankingCard.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            // 카테고리 카드뷰 클릭 시 리스너 메소드 호출
            R.id.card_living_room,
            R.id.card_bedroom,
            R.id.card_kitchen,
            R.id.card_bathroom,
            R.id.card_ranking
                -> listener?.onCategoryViewClicked(v.id)
        }
    }

}