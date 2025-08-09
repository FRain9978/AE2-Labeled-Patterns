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
In the left toolbar, you can switch block-input mode to control if the input field is enable or not.  
Also, you can open the label list button to show all labels this labeler has saved.  
In the label list GUI, you can select any label to be the current label, and delete any label you don't need anymore.

Besides, you can switch rename function on or off by clicking the rename button.
When rename function is on, every operation will affect name instead of label.
For example, if you set the label to "Machine" and rename function is on,
when you apply the label to a pattern provider,
it will rename the pattern provider to "Machine" instead of applying the label "Machine".

## InGame
Normally, to apply a label, hold the labeler in your hand and do shift right-click on a pattern provider block,
you can receive a message in chat to confirm the label is applied.
If you are using a tooltip mod like jade,
you can see the info about applied label in the tooltip of the pattern provider block.

Labeler has 5 modes which can be switched by holding **Shift**(default, keybinding name is "mouse wheel item modifier 1") and scrolling mouse wheel up or down.  
The modes are:
1. **Single Set**: default mode, shift right-click on a pattern provider block to apply label
2. **Single Clear**: shift right-click on a pattern provider block to clear label
3. **Area Set**: shift right click on two blocks to define an area, then all blocks in the area will be checked and applied with the current label
4. **Area Clear**: shift right click on two blocks to define an area, then all blocks in the area will be checked and cleared
5. **Copy**: shift right-click on a pattern provider block to copy its label, it will set the current label of labeler to the copied label and also be saved in the label list.

Besides, you can easily change the current label according to saved labels by holding **Alt**(default, keybinding name is "mouse wheel item modifier 2") and scrolling mouse wheel up or down.