package com.liglp.pvpoverhaul.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.List;

/**
 * Scrollable list with up to two widgets per row.
 */
public class OptionList extends ContainerObjectSelectionList<OptionList.Row> {
	public OptionList(Minecraft minecraft, int width, HeaderAndFooterLayout layout) {
		super(minecraft, width, layout.getContentHeight(), layout.getHeaderHeight(), 25);
	}

	public void addRow(List<AbstractWidget> widgets) {
		this.addEntry(new Row(List.copyOf(widgets)));
	}

	/**
	 * Adds the widgets two per row.
	 */
	public void addPairs(List<AbstractWidget> widgets) {
		for (int i = 0; i < widgets.size(); i += 2) {
			addRow(widgets.subList(i, Math.min(i + 2, widgets.size())));
		}
	}

	@Override
	public int getRowWidth() {
		return 310;
	}

	public class Row extends ContainerObjectSelectionList.Entry<Row> {
		private final List<AbstractWidget> widgets;

		Row(List<AbstractWidget> widgets) {
			this.widgets = widgets;
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return widgets;
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return widgets;
		}

		@Override
		public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float delta) {
			int x = OptionList.this.getWidth() / 2 - 155;
			for (AbstractWidget widget : widgets) {
				widget.setPosition(x, this.getContentY());
				widget.render(graphics, mouseX, mouseY, delta);
				x += 160;
			}
		}
	}
}
