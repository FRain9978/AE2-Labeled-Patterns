---
navigation:
  title: Labeler
  icon: ae2labeledpatterns:labeler
  parent: ae2lps_intro.md
item_ids:
  - ae2labeledpatterns:labeler
---

# Labeler
<ItemImage id="ae2labeledpatterns:labeler" scale="4"></ItemImage>
The Labeler is a tool to manage labels and apply or clear labels on pattern providers.

## GUI
Right click in the air to open the labeler GUI.

In the GUI, in the center area you can see an input field to enter a label, a button to save the label. Make sure save the label after you edit it.  
In the left toolbar, you can switch block-input mode to control if input field is enable or not.  
Also, you can open label list button to show all labels this labeler has saved.  
In the label list GUI, you can select any label to be current label, and delete any label you don't need anymore.

## InGame
Normally, to apply a label, hold the labeler in your hand and do shift right click on a pattern provider block, you can receive a message in chat to confirm the label is applied. If you are using a tooltip mod like jade, you can see the info about applied label in the tooltip of the pattern provider block.

Labeler has 5 modes which can be switched by holding **shift**(default, keybinding name is "mouse wheel item modifier 1") and scrolling mouse wheel up or down.  
The modes are:
1. **Single set**: default mode, shift right click on a pattern provider block to apply label
2. **Single clear**: shift right click on a pattern provider block to clear label
3. **Area set**: shift right click on two blocks to define an area, then all blocks in the area will be checked and applied with the current label
4. **Area clear**: shift right click on two blocks to define an area, then all blocks in the area will be checked and cleared
5. **Copy**: shift right click on a pattern provider block to copy its label, it will set the current label of labeler to the copied label and also be saved in label list.

Besides, you can easily change current label according to saved labels by holding **alt**(default, keybinding name is "mouse wheel item modifier 2") and scrolling mouse wheel up or down.