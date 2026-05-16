package com.ixuea.courses.mymusic.component.product.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.ixuea.courses.mymusic.R;
import com.ixuea.courses.mymusic.adapter.BasePagingDataAdapter;
import com.ixuea.courses.mymusic.component.product.model.Product;
import com.ixuea.courses.mymusic.component.product.ui.ProductViewModel;
import com.ixuea.courses.mymusic.config.Config;
import com.ixuea.courses.mymusic.util.Constant;
import com.ixuea.courses.mymusic.util.ImageUtil;

/**
 * 商城适配器
 */
public class ProductAdapter extends BasePagingDataAdapter<Product, ProductAdapter.ViewHolder> {

    /**
     * PagingDataAdapter需要的数据比较器
     * <p>
     * 简单理解来说就是，如何比较两个对象是否相等
     */
    public static final DiffUtil.ItemCallback COMPARATOR = new DiffUtil.ItemCallback<Product>() {
        /**
         * 如何判断两个Product是否相同
         *
         * 根据id判断，如果id一样就表示同一个对象
         * @param oldItem
         * @param newItem
         * @return
         */
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        /**
         * 如何判断同一个对象，内容是相同
         *
         * 例如对于这个列表同一个product对象，如果标题，图标，购买人数，价格其中一个不一样，就表示内容不一样
         * 可以使用Product实现的比较器
         * @param oldItem
         * @param newItem
         * @return
         */
        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.equals(newItem);
        }
    };
    private final ProductViewModel viewModel;
    private boolean isListStyle = true;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constant.VALUE0) {
            return new ViewHolder(getInflater().inflate(R.layout.item_product, parent, false));
        } else {
            return new ViewHolder(getInflater().inflate(R.layout.item_product_grid, parent, false));
        }
    }

    public ProductAdapter(Context context, ProductViewModel viewModel) {
        super(COMPARATOR, context);
        this.viewModel = viewModel;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        return isListStyle ? Constant.VALUE0 : Constant.VALUE10;
    }

    /**
     * 更改列表样式
     *
     * @param isListStyle
     */
    public void setListStyle(boolean isListStyle) {
        this.isListStyle = isListStyle;

        //不用刷新，因为更改了setSpanCount，会重新调用onCreateViewHolder方法
    }

    class ViewHolder extends BasePagingDataAdapter.ViewHolder<Product> {
        private ImageView iconView;
        private TextView titleView;
        private TextView highlightView;
        private TextView priceView;
        private TextView buyCountView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.detail(getLayoutPosition());
                }
            });

            iconView = view.findViewById(R.id.icon);
            titleView = view.findViewById(R.id.title);
            highlightView = view.findViewById(R.id.highlight);
            priceView = view.findViewById(R.id.price);
            buyCountView = view.findViewById(R.id.buy_count);
        }

        @Override
        public void bind(Product data) {
            super.bind(data);
            String[] icons = data.getIcons();

            ImageUtil.show(getContext(), iconView, icons[0]);

            if (Config.DEBUG) {
                //添加id是方便查看列表是否有重复数据
                titleView.setText(String.format("%s-%s", data.getId(), data.getTitle()));
            } else {
                titleView.setText(data.getTitle());
            }

            highlightView.setText(data.getHighlight());

            //价格
            String price = getContext().getResources().getString(R.string.price, data.getPrice());
            priceView.setText(price);

            buyCountView.setText(getContext().getResources().getString(R.string.buy_count, data.getOrdersCount()));

        }
    }
}
