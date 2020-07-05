package com.dummycoding.mycrypto.adapters;

public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
    void onDragReleased();
}
