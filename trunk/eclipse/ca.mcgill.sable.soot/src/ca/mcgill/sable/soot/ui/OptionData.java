/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package ca.mcgill.sable.soot.ui;

public class OptionData {

	private String text;
	private String parentAlias;
	private String alias;
	private String tooltip;
	private boolean defaultVal;
	private String realAlias;
	private String initText;
	
	/**
	 * Constructor for OptionData.
	 */
	public OptionData() {
		super();
	}
	
	
	/**
	 * Constructor for OptionData.
	 */
	public OptionData(String text, String phaseAlias, String parentAlias, String alias, String tooltip) {
		super();
		setText(text);
		setParentAlias(phaseAlias+" "+parentAlias);
		setAlias(alias);
		setRealAlias(getParentAlias()+" "+getAlias());
		setRealAlias(getRealAlias().trim());
		setTooltip(tooltip);
		setDefaultVal(false);
	}
	
	/**
	 * Constructor for OptionData.
	 */
	public OptionData(String text, String phaseAlias, String parentAlias, String alias, String tooltip, boolean defaultVal) {
		super();
		setText(text);
		setParentAlias(phaseAlias+" "+parentAlias);
		setAlias(alias);
		setRealAlias(getParentAlias()+" "+getAlias());
		setRealAlias(getRealAlias().trim());
		setTooltip(tooltip);
		setDefaultVal(defaultVal);
	}
	
	/**
	 * Constructor for OptionData.
	 */
	public OptionData(String text, String phaseAlias, String parentAlias, String alias, String tooltip, String initText) {
		super();
		setText(text);
		setParentAlias(phaseAlias+" "+parentAlias);
		setAlias(alias);
		setRealAlias(getParentAlias()+" "+getAlias());
		setRealAlias(getRealAlias().trim());
		setTooltip(tooltip);
		setDefaultVal(false);
		setInitText(initText);
	}
	
	/**
	 * Constructor for OptionData.
	 */
	public OptionData(String text, String alias, String tooltip, boolean defaultVal) {
		super();
		setText(text);
		setAlias(alias);
		setRealAlias(getAlias());
		setRealAlias(getRealAlias().trim());
		setTooltip(tooltip);
		setDefaultVal(defaultVal);
	}
	
		
	/**
	 * Returns the alias.
	 * @return String
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Returns the text.
	 * @return String
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the tooltip.
	 * @return String
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Sets the alias.
	 * @param alias The alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Sets the text.
	 * @param text The text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Sets the tooltip.
	 * @param tooltip The tooltip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * Returns the defaultVal.
	 * @return boolean
	 */
	public boolean isDefaultVal() {
		return defaultVal;
	}

	/**
	 * Sets the defaultVal.
	 * @param defaultVal The defaultVal to set
	 */
	public void setDefaultVal(boolean defaultVal) {
		this.defaultVal = defaultVal;
	}

	/**
	 * Returns the parentAlias.
	 * @return String
	 */
	public String getParentAlias() {
		return parentAlias;
	}

	/**
	 * Sets the parentAlias.
	 * @param parentAlias The parentAlias to set
	 */
	public void setParentAlias(String parentAlias) {
		this.parentAlias = parentAlias;
	}

	/**
	 * Returns the realAlias.
	 * @return String
	 */
	public String getRealAlias() {
		return realAlias;
	}

	/**
	 * Sets the realAlias.
	 * @param realAlias The realAlias to set
	 */
	public void setRealAlias(String realAlias) {
		this.realAlias = realAlias;
	}

	/**
	 * Returns the initText.
	 * @return String
	 */
	public String getInitText() {
		return initText;
	}

	/**
	 * Sets the initText.
	 * @param initText The initText to set
	 */
	public void setInitText(String initText) {
		this.initText = initText;
	}

}
