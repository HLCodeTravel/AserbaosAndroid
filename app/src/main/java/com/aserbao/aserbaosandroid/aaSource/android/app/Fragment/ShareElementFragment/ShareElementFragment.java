package com.aserbao.aserbaosandroid.aaSource.android.app.Fragment.ShareElementFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aserbao.aserbaosandroid.R;
import com.aserbao.aserbaosandroid.aaSource.android.app.Fragment.FragmentActivity;
import com.aserbao.aserbaosandroid.aaSource.android.app.Fragment.ShareElementFragment.adapter.GridAdapter;
import com.aserbao.aserbaosandroid.comon.base.beans.BaseRecyclerBean;
import com.aserbao.aserbaosandroid.comon.base.interfaces.IBaseRecyclerItemClickListener;
import com.aserbao.aserbaosandroid.comon.commonData.ASourceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能:
 *
 * @author aserbao
 * @date : On 2019-08-19 10:54
 * @project:AserbaosAndroid
 * @package:com.aserbao.aserbaosandroid.aaSource.android.app.Fragment.ShareElementFragment
 */
public class ShareElementFragment extends Fragment implements IBaseRecyclerItemClickListener {

//    @BindView(R.id.opengl_recycler_view)
    public RecyclerView  mBaseRecyclerView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*View view = LayoutInflater.from(getContext()).inflate(R.layout.base_recyclerview_activity, container, false);
        ButterKnife.bind(this,view);
        initViewForLinear();*/
        mBaseRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_grid, container, false);
        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        mBaseRecyclerView.setAdapter(new GridAdapter(this));


        prepareTransitions();
        postponeEnterTransition();
        return mBaseRecyclerView;
    }

    private void prepareTransitions() {
        setExitTransition(TransitionInflater.from(getContext())
            .inflateTransition(R.transition.grid_exit_transition));
        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
            new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    // Locate the ViewHolder for the clicked position.
                    RecyclerView.ViewHolder selectedViewHolder = mBaseRecyclerView
                        .findViewHolderForAdapterPosition(FragmentActivity.currentPosition);
                    if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                        return;
                    }

                    // Map the first shared element name to the child ImageView.
                    sharedElements.put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.rv_image_item_card_view));
                }
            });
    }


    public int mOrientation = LinearLayoutManager.VERTICAL;
    List<BaseRecyclerBean> mBaseRecyclerBean = new ArrayList<>();

    public void initView() {
        mBaseRecyclerBean = ASourceUtil.getStaticRecyclerViewData(mBaseRecyclerBean);
//        ShareElementFragmentAdapter mCommonAdapter = new ShareElementFragmentAdapter(getContext(), getActivity(), mBaseRecyclerBean, this);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext(), mOrientation, false);
        mBaseRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBaseRecyclerView.setAdapter(new GridAdapter(this));
//        mBaseRecyclerViewFl.setBackgroundResource(ASourceUtil.getRandomImageId());
    }


    @Override
    public void itemClickBack(View view, int position) {
        ((TransitionSet) getExitTransition()).excludeTarget(view, true);

        ImageView transitioningView = view.findViewById(R.id.image_view_item);
        getFragmentManager()
            .beginTransaction()
            .setReorderingAllowed(true) // Optimize for shared element transition
            .addSharedElement(transitioningView, transitioningView.getTransitionName())
            .replace(R.id.base_recycler_empty_container, new ImagePagerFragment(), ImagePagerFragment.class
                .getSimpleName())
            .addToBackStack(null)
            .commit();

    }
}