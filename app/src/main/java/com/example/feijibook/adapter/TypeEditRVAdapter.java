package com.example.feijibook.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.helper.OnDragVHListener;
import com.example.feijibook.helper.OnItemMoveListener;
import com.example.feijibook.util.SoundShakeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 你是我的 on 2019/3/27
 */
public class TypeEditRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnItemMoveListener {
    private ItemTouchHelper mItemTouchHelper;

    /**
     * 可添加类型列表
     */
    private List<AddtiveType> mAddtiveTypeList = new ArrayList<>();
    /**
     * 可选择类型列表
     */
    private List<OptionalType> mOptionalTypeList = new ArrayList<>();

    private LayoutInflater mInflater;

    private static final int OPTIONAL_TYPE = 0;
    private static final int MORE_HEARDER = 1;
    private static final int ADDDTIVR_TYPE = 2;
    private static int COUNT_HEADER = 1;

    private UpdateTypeList mUpdateTypeList;

    public TypeEditRVAdapter(Context context, ItemTouchHelper itemTouchHelper) {
        mInflater = LayoutInflater.from(context);
        mItemTouchHelper = itemTouchHelper;
    }

    public void setList(List<OptionalType> optionalTypeList, List<AddtiveType> addtiveTypeList) {
        mAddtiveTypeList = addtiveTypeList;
        mOptionalTypeList = optionalTypeList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= 0 && position < mOptionalTypeList.size()) {
            return OPTIONAL_TYPE;
        }// 如果没有可添加类型，则不显示"更多选择"标题
        else if (position == mOptionalTypeList.size() && mAddtiveTypeList.size() > 0) {
            return MORE_HEARDER;
        } else {
            return ADDDTIVR_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        switch (i) {
            case OPTIONAL_TYPE:
                view = mInflater.inflate(R.layout.item_optional_type, viewGroup, false);
                final OptionalTypeHolder optionalTypeHolder = new OptionalTypeHolder(view, this);

                optionalTypeHolder.ivRemoveOptionalToAddible.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundShakeUtil.playSound(SoundShakeUtil.DELETE_SOUND);
                        moveOptionalToAddible(optionalTypeHolder);
                        mUpdateTypeList.update(mOptionalTypeList, mAddtiveTypeList);
                    }
                });
                return optionalTypeHolder;
            case MORE_HEARDER:
                view = mInflater.inflate(R.layout.item_more_title, viewGroup, false);
                return new RecyclerView.ViewHolder(view) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            case ADDDTIVR_TYPE:
                view = mInflater.inflate(R.layout.item_addible_type, viewGroup, false);
                final AddibleHolder addibleHolder = new AddibleHolder(view);

                addibleHolder.ivAddAddibleToOptional.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SOUND);
                        moveAddibleToOptional(addibleHolder);
                        mUpdateTypeList.update(mOptionalTypeList, mAddtiveTypeList);
                    }
                });
                return addibleHolder;
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof OptionalTypeHolder) {
            final OptionalTypeHolder optionalTypeHolder = (OptionalTypeHolder) viewHolder;
            OptionalType optionalType = mOptionalTypeList.get(i);
            if (optionalType.getDefaultOrCustom_O().equals("d")) {
                optionalTypeHolder.tvOptionalTypeName.setText(optionalType.getTypeName_O());
            } else {
                optionalTypeHolder.tvOptionalTypeName.setText(optionalType.getCustomTypeName());
            }
            optionalTypeHolder.ivOptionalType.setImageResource(optionalType.getTypeIconUrl_O());

            optionalTypeHolder.ivDragItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mItemTouchHelper.startDrag(optionalTypeHolder);
                    return false;
                }
            });
        } else if (viewHolder instanceof AddibleHolder) {
            AddibleHolder addibleHolder = (AddibleHolder) viewHolder;
            AddtiveType addtiveType = mAddtiveTypeList.get(i - mOptionalTypeList.size() - COUNT_HEADER);
            addibleHolder.tvAddibleTypeName.setText(addtiveType.getTypeName_A());
            addibleHolder.ivAddibleType.setImageResource(addtiveType.getTypeIconUrl_A());
        }
    }

    @Override
    public int getItemCount() {
        if (mAddtiveTypeList.size() > 0) {
            return mOptionalTypeList.size() + mAddtiveTypeList.size() + COUNT_HEADER;
        }
        return mAddtiveTypeList.size() + mOptionalTypeList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        OptionalType item = mOptionalTypeList.get(fromPosition);
        mOptionalTypeList.remove(fromPosition);
        mOptionalTypeList.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 可选类型移除到可添加类型
     *
     * @param holder 可选类型的viewholder
     */
    private void moveOptionalToAddible(OptionalTypeHolder holder) {
        int position = holder.getAdapterPosition();

        if (position > mOptionalTypeList.size() - 1) {
            return;
        }
        OptionalType item = mOptionalTypeList.get(position);
        if (item.getDefaultOrCustom_O().equals("d")) {
            AddtiveType addtiveType = new AddtiveType();
            addtiveType.setTypeName_A(item.getTypeName_O());
            addtiveType.setIncomeOrExpend_A(item.getIncomeOrExpend_O());
            addtiveType.setTypeIconUrl_A(item.getTypeIconUrl_O());

            mOptionalTypeList.remove(item);
            mAddtiveTypeList.add(0, addtiveType);
            notifyItemMoved(position, mOptionalTypeList.size() + COUNT_HEADER);
        }// 不是默认类型，则直接删除
        else {
            mOptionalTypeList.remove(item);
            notifyItemRemoved(position);
        }
    }

    /**
     * 可添加类型 添加到 可选类型
     *
     * @param addibleHolder 可添加类型的viewholder
     */
    private void moveAddibleToOptional(AddibleHolder addibleHolder) {
        int position = processItemRemoveAdd(addibleHolder);
        if (position == -1) {
            return;
        }
        if (position == mOptionalTypeList.size() + mAddtiveTypeList.size()) {
            notifyDataSetChanged();
            return;
        }
        notifyItemMoved(position, mOptionalTypeList.size() - 1);
    }

    private int processItemRemoveAdd(AddibleHolder addibleHolder) {
        int position = addibleHolder.getAdapterPosition();

        int startPosition = position - mOptionalTypeList.size() - COUNT_HEADER;
        if (startPosition > mAddtiveTypeList.size() - 1 || startPosition < 0) {
            return -1;
        }
        AddtiveType item = mAddtiveTypeList.get(startPosition);
        OptionalType optionalType = new OptionalType();
        optionalType.setDefaultOrCustom_O("d");
        optionalType.setTypeName_O(item.getTypeName_A());
        optionalType.setCustomTypeName(null);
        optionalType.setIncomeOrExpend_O(item.getIncomeOrExpend_A());
        optionalType.setTypeIconUrl_O(item.getTypeIconUrl_A());
        mAddtiveTypeList.remove(startPosition);
        mOptionalTypeList.add(optionalType);
        return position;
    }

    static
    class OptionalTypeHolder extends RecyclerView.ViewHolder implements OnDragVHListener {
        @BindView(R.id.iv_remove_optional_to_addible)
        ImageView ivRemoveOptionalToAddible;
        @BindView(R.id.iv_optional_type)
        ImageView ivOptionalType;
        @BindView(R.id.tv_optional_type_name)
        TextView tvOptionalTypeName;
        @BindView(R.id.iv_drag_item)
        ImageView ivDragItem;
        TypeEditRVAdapter mAdapter;

        OptionalTypeHolder(View view, TypeEditRVAdapter adapter) {
            super(view);
            ButterKnife.bind(this, view);
            mAdapter = adapter;
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemFinish() {
            mAdapter.mUpdateTypeList.update(mAdapter.mOptionalTypeList, mAdapter.mAddtiveTypeList);
        }
    }

    static
    class AddibleHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_add_addible_to_optional)
        ImageView ivAddAddibleToOptional;
        @BindView(R.id.iv_addible_type)
        ImageView ivAddibleType;
        @BindView(R.id.tv_addible_type_name)
        TextView tvAddibleTypeName;

        AddibleHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 编辑类型列表，同步更新
     */
    public interface UpdateTypeList {
        /**
         * 更新
         *
         * @param optionalTypes 可选择类型列表
         * @param addtiveTypes  可添加类型列表
         */
        void update(List<OptionalType> optionalTypes, List<AddtiveType> addtiveTypes);
    }

    public void setUpdateTypeList(UpdateTypeList updateTypeList) {
        mUpdateTypeList = updateTypeList;
    }
}
