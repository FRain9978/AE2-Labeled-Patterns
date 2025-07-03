package org.ae2LabeledPatterns.blocks;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;

public class MyBlockEntityListener implements IGridNodeListener<MyBlockEntity> {
    public static final MyBlockEntityListener INSTANCE = new MyBlockEntityListener();

    @Override
    public void onStateChanged(MyBlockEntity nodeOwner, IGridNode node, State state)
    {
        // for example: change block state of nodeOwner to indicate state
        // send node owner to clients
    }

    @Override
    public void onSaveChanges(MyBlockEntity nodeOwner, IGridNode node)
    {

    }
}
