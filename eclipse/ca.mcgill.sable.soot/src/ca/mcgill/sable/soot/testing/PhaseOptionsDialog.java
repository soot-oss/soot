

/*
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
 *
 * This class is generated automajically from xml - DO NOT EDIT - as
 * changes will be over written
 * 
 * The purpose of this class is to automajically generate a options
 * dialog in the event that options change
 * 
 * Taking options away - should not damage the dialog
 * Adding new sections of options - should not damage the dialog
 * Adding new otpions to sections (of known option type) - should not
 * damage the dialog
 *
 * Adding new option types will break the dialog (option type widgets
 * will need to be created)
 *
 */

package ca.mcgill.sable.soot.testing;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.ui.*;
import ca.mcgill.sable.soot.util.*;
import java.util.HashMap;

public class PhaseOptionsDialog extends AbstractOptionsDialog {

	public PhaseOptionsDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * each section gets initialize as a stack layer in pageContainer
	 * the area containing the options
	 */ 
	protected void initializePageContainer() {


Composite General_OptionsChild = General_OptionsCreate(getPageContainer());

Composite Input_OptionsChild = Input_OptionsCreate(getPageContainer());

Composite Output_OptionsChild = Output_OptionsCreate(getPageContainer());

Composite Processing_OptionsChild = Processing_OptionsCreate(getPageContainer());

Composite Single_File_Mode_OptionsChild = Single_File_Mode_OptionsCreate(getPageContainer());

Composite Application_Mode_OptionsChild = Application_Mode_OptionsCreate(getPageContainer());

Composite Input_Attribute_OptionsChild = Input_Attribute_OptionsCreate(getPageContainer());

Composite Annotation_OptionsChild = Annotation_OptionsCreate(getPageContainer());

Composite Miscellaneous_OptionsChild = Miscellaneous_OptionsCreate(getPageContainer());

Composite jbChild = jbCreate(getPageContainer());

Composite cgChild = cgCreate(getPageContainer());

Composite wstpChild = wstpCreate(getPageContainer());

Composite wsopChild = wsopCreate(getPageContainer());

Composite wjtpChild = wjtpCreate(getPageContainer());

Composite wjopChild = wjopCreate(getPageContainer());

Composite wjapChild = wjapCreate(getPageContainer());

Composite stpChild = stpCreate(getPageContainer());

Composite sopChild = sopCreate(getPageContainer());

Composite jtpChild = jtpCreate(getPageContainer());

Composite jopChild = jopCreate(getPageContainer());

Composite japChild = japCreate(getPageContainer());

Composite gbChild = gbCreate(getPageContainer());

Composite gopChild = gopCreate(getPageContainer());

Composite bbChild = bbCreate(getPageContainer());

Composite bopChild = bopCreate(getPageContainer());

Composite tagChild = tagCreate(getPageContainer());

Composite jbjb_asvChild = jbjb_asvCreate(getPageContainer());

Composite jbjb_ulpChild = jbjb_ulpCreate(getPageContainer());

Composite jbjb_lnsChild = jbjb_lnsCreate(getPageContainer());

Composite jbjb_cpChild = jbjb_cpCreate(getPageContainer());

Composite jbjb_daeChild = jbjb_daeCreate(getPageContainer());

Composite jbjb_lsChild = jbjb_lsCreate(getPageContainer());

Composite jbjb_aChild = jbjb_aCreate(getPageContainer());

Composite jbjb_uleChild = jbjb_uleCreate(getPageContainer());

Composite jbjb_trChild = jbjb_trCreate(getPageContainer());

Composite jbjb_cp_uleChild = jbjb_cp_uleCreate(getPageContainer());

Composite jbjb_lpChild = jbjb_lpCreate(getPageContainer());

Composite jbjb_neChild = jbjb_neCreate(getPageContainer());

Composite jbjb_uceChild = jbjb_uceCreate(getPageContainer());

Composite cgcg_oldchaChild = cgcg_oldchaCreate(getPageContainer());

Composite cgcg_vtaChild = cgcg_vtaCreate(getPageContainer());

Composite cgcg_chaChild = cgcg_chaCreate(getPageContainer());

Composite cgcg_sparkChild = cgcg_sparkCreate(getPageContainer());

Composite cgSpark_General_OptionsChild = cgSpark_General_OptionsCreate(getPageContainer());

Composite cgSpark_Pointer_Assignment_Graph_Building_OptionsChild = cgSpark_Pointer_Assignment_Graph_Building_OptionsCreate(getPageContainer());

Composite cgSpark_Pointer_Assignment_Graph_Simplification_OptionsChild = cgSpark_Pointer_Assignment_Graph_Simplification_OptionsCreate(getPageContainer());

Composite cgSpark_Points_To_Set_Flowing_OptionsChild = cgSpark_Points_To_Set_Flowing_OptionsCreate(getPageContainer());

Composite cgSpark_Output_OptionsChild = cgSpark_Output_OptionsCreate(getPageContainer());

Composite wjopwjop_smbChild = wjopwjop_smbCreate(getPageContainer());

Composite wjopwjop_siChild = wjopwjop_siCreate(getPageContainer());

Composite wjapwjap_raChild = wjapwjap_raCreate(getPageContainer());

Composite jopjop_cseChild = jopjop_cseCreate(getPageContainer());

Composite jopjop_bcmChild = jopjop_bcmCreate(getPageContainer());

Composite jopjop_lcmChild = jopjop_lcmCreate(getPageContainer());

Composite jopjop_cpChild = jopjop_cpCreate(getPageContainer());

Composite jopjop_cpfChild = jopjop_cpfCreate(getPageContainer());

Composite jopjop_cbfChild = jopjop_cbfCreate(getPageContainer());

Composite jopjop_daeChild = jopjop_daeCreate(getPageContainer());

Composite jopjop_uce1Child = jopjop_uce1Create(getPageContainer());

Composite jopjop_uce2Child = jopjop_uce2Create(getPageContainer());

Composite jopjop_ubf1Child = jopjop_ubf1Create(getPageContainer());

Composite jopjop_ubf2Child = jopjop_ubf2Create(getPageContainer());

Composite jopjop_uleChild = jopjop_uleCreate(getPageContainer());

Composite japjap_npcChild = japjap_npcCreate(getPageContainer());

Composite japjap_abcChild = japjap_abcCreate(getPageContainer());

Composite japjap_profilingChild = japjap_profilingCreate(getPageContainer());

Composite japjap_seaChild = japjap_seaCreate(getPageContainer());

Composite japjap_fieldrwChild = japjap_fieldrwCreate(getPageContainer());

Composite gbgb_a1Child = gbgb_a1Create(getPageContainer());

Composite gbgb_cfChild = gbgb_cfCreate(getPageContainer());

Composite gbgb_a2Child = gbgb_a2Create(getPageContainer());

Composite gbgb_uleChild = gbgb_uleCreate(getPageContainer());

Composite bbbb_lsoChild = bbbb_lsoCreate(getPageContainer());

Composite bbbb_phoChild = bbbb_phoCreate(getPageContainer());

Composite bbbb_uleChild = bbbb_uleCreate(getPageContainer());

Composite bbbb_lpChild = bbbb_lpCreate(getPageContainer());

Composite tagtag_lnChild = tagtag_lnCreate(getPageContainer());

Composite tagtag_anChild = tagtag_anCreate(getPageContainer());

Composite tagtag_depChild = tagtag_depCreate(getPageContainer());

Composite tagtag_fieldrwChild = tagtag_fieldrwCreate(getPageContainer());

		
	}
	
	/**
	 * all options get saved as <alias, value> pair
	 */ 
	protected void okPressed() {
			createNewConfig();	
		super.okPressed();
	}

	private void createNewConfig() {
	
		setConfig(new HashMap());
		
		boolean boolRes = false;
		String stringRes = "";
		boolean defBoolRes = false;
		String defStringRes = "";
		
		
		boolRes = getGeneral_Optionshelp_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getGeneral_Optionshelp_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getGeneral_Optionsversion_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getGeneral_Optionsversion_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getGeneral_Optionsverbose_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getGeneral_Optionsverbose_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getGeneral_Optionsapp_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getGeneral_Optionsapp_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getGeneral_Optionswhole_program_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getGeneral_Optionswhole_program_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getInput_Optionsallow_phantom_refs_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getInput_Optionsallow_phantom_refs_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getInput_Optionssoot_classpath_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getInput_Optionssoot_classpath_widget().getAlias(), stringRes);
		}
		 
		stringRes = getInput_Optionssrc_prec_widget().getSelectedAlias();

		
		defStringRes = "c";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getInput_Optionssrc_prec_widget().getAlias(), stringRes);
		}
		
		boolRes = getOutput_Optionsvia_grimp_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getOutput_Optionsvia_grimp_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getOutput_Optionsxml_attributes_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getOutput_Optionsxml_attributes_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getOutput_Optionsoutput_dir_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getOutput_Optionsoutput_dir_widget().getAlias(), stringRes);
		}
		 
		stringRes = getOutput_Optionsoutput_format_widget().getSelectedAlias();

		
		defStringRes = "c";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getOutput_Optionsoutput_format_widget().getAlias(), stringRes);
		}
		
		boolRes = getProcessing_Optionsoptimize_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getProcessing_Optionsoptimize_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getProcessing_Optionswhole_optimize_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getProcessing_Optionswhole_optimize_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getProcessing_Optionsvia_shimple_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getProcessing_Optionsvia_shimple_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbno_splitting_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbno_splitting_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbno_typing_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbno_typing_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbaggregate_all_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbaggregate_all_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbno_aggregating_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbno_aggregating_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbuse_original_names_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbuse_original_names_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbpack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbpack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbno_cp_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbno_cp_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbno_nop_elimination_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbno_nop_elimination_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbno_unreachable_code_elimination_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbno_unreachable_code_elimination_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbverbatim_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbverbatim_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_asvdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_asvdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_asvonly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_asvonly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_ulpdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_ulpdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_ulpunsplit_original_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_ulpunsplit_original_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lnsdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lnsdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lnsonly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lnsonly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_cpdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_cpdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_cponly_regular_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_cponly_regular_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_cponly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_cponly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_daedisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_daedisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_daeonly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_daeonly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lsdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lsdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_adisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_adisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_aonly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_aonly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_uledisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_uledisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_trdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_trdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_cp_uledisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_cp_uledisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lpdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lpdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lpunsplit_original_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lpunsplit_original_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_nedisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_nedisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_ucedisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_ucedisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_oldchadisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_oldchadisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_vtadisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_vtadisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getcgcg_vtapasses_widget().getText().getText();
		
		defStringRes = "1";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getcgcg_vtapasses_widget().getAlias(), stringRes);
		}
		
		boolRes = getcgcg_chadisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_chadisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_chaverbose_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_chaverbose_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkverbose_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkverbose_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkignore_types_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkignore_types_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkforce_gc_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkforce_gc_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkpre_jimplify_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkpre_jimplify_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkvta_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkvta_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkrta_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkrta_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkfield_based_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkfield_based_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparktypes_for_sites_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparktypes_for_sites_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkmerge_stringbuffer_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkmerge_stringbuffer_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparksimulate_natives_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparksimulate_natives_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparksimple_edges_bidirectional_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparksimple_edges_bidirectional_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkon_fly_cg_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkon_fly_cg_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkparms_as_fields_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkparms_as_fields_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkreturns_as_fields_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkreturns_as_fields_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparksimplify_offline_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparksimplify_offline_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparksimplify_sccs_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparksimplify_sccs_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkignore_types_for_sccs_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkignore_types_for_sccs_widget().getAlias(), new Boolean(boolRes));
		}
		 
		stringRes = getcgcg_sparkpropagator_widget().getSelectedAlias();

		
		defStringRes = "worklist";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getcgcg_sparkpropagator_widget().getAlias(), stringRes);
		}
		 
		stringRes = getcgcg_sparkset_impl_widget().getSelectedAlias();

		
		defStringRes = "double";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getcgcg_sparkset_impl_widget().getAlias(), stringRes);
		}
		 
		stringRes = getcgcg_sparkdouble_set_old_widget().getSelectedAlias();

		
		defStringRes = "hybrid";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getcgcg_sparkdouble_set_old_widget().getAlias(), stringRes);
		}
		 
		stringRes = getcgcg_sparkdouble_set_new_widget().getSelectedAlias();

		
		defStringRes = "hybrid";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getcgcg_sparkdouble_set_new_widget().getAlias(), stringRes);
		}
		
		boolRes = getcgcg_sparkdump_html_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkdump_html_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkdump_pag_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkdump_pag_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkdump_solution_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkdump_solution_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparktopo_sort_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparktopo_sort_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkdump_types_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkdump_types_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkclass_method_var_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkclass_method_var_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkdump_answer_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkdump_answer_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparktrim_invoke_graph_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparktrim_invoke_graph_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkadd_tags_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkadd_tags_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwstpdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwstpdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwsopdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwsopdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjtpdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjtpdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjopdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjopwjop_smbdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopwjop_smbdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjopwjop_smbinsert_null_checks_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopwjop_smbinsert_null_checks_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjopwjop_smbinsert_redundant_casts_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopwjop_smbinsert_redundant_casts_widget().getAlias(), new Boolean(boolRes));
		}
		 
		stringRes = getwjopwjop_smballowed_modifier_changes_widget().getSelectedAlias();

		
		defStringRes = "unsafe";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getwjopwjop_smballowed_modifier_changes_widget().getAlias(), stringRes);
		}
		
		boolRes = getwjopwjop_sidisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopwjop_sidisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjopwjop_siinsert_null_checks_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopwjop_siinsert_null_checks_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjopwjop_siinsert_redundant_casts_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopwjop_siinsert_redundant_casts_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getwjopwjop_siexpansion_factor_widget().getText().getText();
		
		defStringRes = "3";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getwjopwjop_siexpansion_factor_widget().getAlias(), stringRes);
		}
		
		stringRes = getwjopwjop_simax_container_size_widget().getText().getText();
		
		defStringRes = "5000";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getwjopwjop_simax_container_size_widget().getAlias(), stringRes);
		}
		
		stringRes = getwjopwjop_simax_inlinee_size_widget().getText().getText();
		
		defStringRes = "20";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getwjopwjop_simax_inlinee_size_widget().getAlias(), stringRes);
		}
		 
		stringRes = getwjopwjop_siallowed_modifier_changes_widget().getSelectedAlias();

		
		defStringRes = "unsafe";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getwjopwjop_siallowed_modifier_changes_widget().getAlias(), stringRes);
		}
		
		boolRes = getwjapdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjapdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjapwjap_radisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjapwjap_radisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getstpdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getstpdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getsopdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getsopdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjtpdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjtpdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_csedisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_csedisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_csenaive_side_effect_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_csenaive_side_effect_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_bcmdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_bcmdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_bcmnaive_side_effect_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_bcmnaive_side_effect_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_lcmdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_lcmdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_lcmunroll_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_lcmunroll_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_lcmnaive_side_effect_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_lcmnaive_side_effect_widget().getAlias(), new Boolean(boolRes));
		}
		 
		stringRes = getjopjop_lcmsafe_widget().getSelectedAlias();

		
		defStringRes = "safe";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getjopjop_lcmsafe_widget().getAlias(), stringRes);
		}
		
		boolRes = getjopjop_cpdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_cpdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_cponly_regular_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_cponly_regular_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_cponly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_cponly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_cpfdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_cpfdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_cbfdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_cbfdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_daedisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_daedisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_daeonly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_daeonly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_uce1disabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_uce1disabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_uce2disabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_uce2disabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_ubf1disabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_ubf1disabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_ubf2disabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_ubf2disabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_uledisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_uledisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_npcdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_npcdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_npconly_array_ref_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_npconly_array_ref_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_npcprofiling_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_npcprofiling_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_abcdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_abcdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_abcwith_all_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_abcwith_all_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_abcwith_fieldref_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_abcwith_fieldref_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_abcwith_arrayref_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_abcwith_arrayref_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_abcwith_cse_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_abcwith_cse_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_abcwith_classfield_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_abcwith_classfield_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_abcwith_rectarray_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_abcwith_rectarray_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_abcprofiling_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_abcprofiling_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_profilingdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_profilingdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_profilingnotmainentry_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_profilingnotmainentry_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_seadisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_seadisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_seanaive_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_seanaive_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_fieldrwdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_fieldrwdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getjapjap_fieldrwthreshold_widget().getText().getText();
		
		defStringRes = "100";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getjapjap_fieldrwthreshold_widget().getAlias(), stringRes);
		}
		
		boolRes = getgbdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_a1disabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_a1disabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_a1only_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_a1only_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_cfdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_cfdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_a2disabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_a2disabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_a2only_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_a2only_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_uledisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_uledisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgopdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgopdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lsodisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lsodisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lsodebug_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lsodebug_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lsointer_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lsointer_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lsosl_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lsosl_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lsosl2_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lsosl2_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lsosll_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lsosll_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lsosll2_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lsosll2_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_phodisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_phodisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_uledisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_uledisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lpdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lpdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lpunsplit_original_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lpunsplit_original_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbopdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbopdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagtag_lndisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagtag_lndisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagtag_andisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagtag_andisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagtag_depdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagtag_depdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagtag_fieldrwdisabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagtag_fieldrwdisabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getSingle_File_Mode_Optionsprocess_path_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getSingle_File_Mode_Optionsprocess_path_widget().getAlias(), stringRes);
		}
		
		boolRes = getApplication_Mode_Optionsanalyze_context_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getApplication_Mode_Optionsanalyze_context_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getApplication_Mode_Optionsinclude_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getApplication_Mode_Optionsinclude_widget().getAlias(), stringRes);
		}
		
		stringRes = getApplication_Mode_Optionsexclude_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getApplication_Mode_Optionsexclude_widget().getAlias(), stringRes);
		}
		
		stringRes = getApplication_Mode_Optionsdynamic_classes_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getApplication_Mode_Optionsdynamic_classes_widget().getAlias(), stringRes);
		}
		
		stringRes = getApplication_Mode_Optionsdynamic_path_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getApplication_Mode_Optionsdynamic_path_widget().getAlias(), stringRes);
		}
		
		stringRes = getApplication_Mode_Optionsdynamic_package_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getApplication_Mode_Optionsdynamic_package_widget().getAlias(), stringRes);
		}
		
		boolRes = getInput_Attribute_Optionskeep_line_number_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getInput_Attribute_Optionskeep_line_number_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getInput_Attribute_Optionskeep_offset_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getInput_Attribute_Optionskeep_offset_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getAnnotation_Optionsannot_nullpointer_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getAnnotation_Optionsannot_nullpointer_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getAnnotation_Optionsannot_arraybounds_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getAnnotation_Optionsannot_arraybounds_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getAnnotation_Optionsannot_side_effect_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getAnnotation_Optionsannot_side_effect_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getAnnotation_Optionsannot_fieldrw_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getAnnotation_Optionsannot_fieldrw_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getMiscellaneous_Optionstime_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getMiscellaneous_Optionstime_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getMiscellaneous_Optionssubtract_gc_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getMiscellaneous_Optionssubtract_gc_widget().getAlias(), new Boolean(boolRes));
		}
		
		
				
	}

	protected HashMap savePressed() {
	

		createNewConfig();
		
		return getConfig();
	}
	


	/**
	 * the initial input of selection tree corresponds to each section
	 * at some point sections will have sub-sections which will be
	 * children of branches (ie phase - options)
	 */ 
	protected SootOption getInitialInput() {
		SootOption root = new SootOption("");
		SootOption parent;
		SootOption subParent;
		SootOption subSectParent;
		

		SootOption General_Options_branch = new SootOption("General Options");
		root.addChild(General_Options_branch);
		parent = General_Options_branch;		
		
		SootOption Input_Options_branch = new SootOption("Input Options");
		root.addChild(Input_Options_branch);
		parent = Input_Options_branch;		
		
		SootOption Output_Options_branch = new SootOption("Output Options");
		root.addChild(Output_Options_branch);
		parent = Output_Options_branch;		
		
		SootOption Processing_Options_branch = new SootOption("Processing Options");
		root.addChild(Processing_Options_branch);
		parent = Processing_Options_branch;		
		
		//Phase Options
			//Jimple Body Creation
			SootOption jb_branch = new SootOption("Jimple Body Creation");
			parent.addChild(jb_branch);
			subParent = jb_branch;

			SootOption jb_jb_asv_branch = new SootOption("Stack Variable Aggregation");
			subParent.addChild(jb_jb_asv_branch);

			
			subSectParent = jb_jb_asv_branch;
			
			
			SootOption jb_jb_ulp_branch = new SootOption("Unsplit-originals Local Packer");
			subParent.addChild(jb_jb_ulp_branch);

			
			subSectParent = jb_jb_ulp_branch;
			
			
			SootOption jb_jb_lns_branch = new SootOption("Local Name Standardizer");
			subParent.addChild(jb_jb_lns_branch);

			
			subSectParent = jb_jb_lns_branch;
			
			
			SootOption jb_jb_cp_branch = new SootOption("Copy Propagator");
			subParent.addChild(jb_jb_cp_branch);

			
			subSectParent = jb_jb_cp_branch;
			
			
			SootOption jb_jb_dae_branch = new SootOption("Dead Assignment Eliminator");
			subParent.addChild(jb_jb_dae_branch);

			
			subSectParent = jb_jb_dae_branch;
			
			
			SootOption jb_jb_ls_branch = new SootOption("Local Splitter");
			subParent.addChild(jb_jb_ls_branch);

			
			subSectParent = jb_jb_ls_branch;
			
			
			SootOption jb_jb_a_branch = new SootOption("Aggregator");
			subParent.addChild(jb_jb_a_branch);

			
			subSectParent = jb_jb_a_branch;
			
			
			SootOption jb_jb_ule_branch = new SootOption("Unused Local Eliminator");
			subParent.addChild(jb_jb_ule_branch);

			
			subSectParent = jb_jb_ule_branch;
			
			
			SootOption jb_jb_tr_branch = new SootOption("Type Assigner");
			subParent.addChild(jb_jb_tr_branch);

			
			subSectParent = jb_jb_tr_branch;
			
			
			SootOption jb_jb_cp_ule_branch = new SootOption("Unused Local Eliminator");
			subParent.addChild(jb_jb_cp_ule_branch);

			
			subSectParent = jb_jb_cp_ule_branch;
			
			
			SootOption jb_jb_lp_branch = new SootOption("Local Packer");
			subParent.addChild(jb_jb_lp_branch);

			
			subSectParent = jb_jb_lp_branch;
			
			
			SootOption jb_jb_ne_branch = new SootOption("Nop Eliminator");
			subParent.addChild(jb_jb_ne_branch);

			
			subSectParent = jb_jb_ne_branch;
			
			
			SootOption jb_jb_uce_branch = new SootOption("Unreachable Code Eliminator");
			subParent.addChild(jb_jb_uce_branch);

			
			subSectParent = jb_jb_uce_branch;
			
			
			//Call Graph
			SootOption cg_branch = new SootOption("Call Graph");
			parent.addChild(cg_branch);
			subParent = cg_branch;

			SootOption cg_cg_oldcha_branch = new SootOption("Old Class Hierarchy Analysis");
			subParent.addChild(cg_cg_oldcha_branch);

			
			subSectParent = cg_cg_oldcha_branch;
			
			
			SootOption cg_cg_vta_branch = new SootOption("Variable Type Analysis");
			subParent.addChild(cg_cg_vta_branch);

			
			subSectParent = cg_cg_vta_branch;
			
			
			SootOption cg_cg_cha_branch = new SootOption("Spark Class Hierarchy Analysis");
			subParent.addChild(cg_cg_cha_branch);

			
			subSectParent = cg_cg_cha_branch;
			
			
			SootOption cg_cg_spark_branch = new SootOption("Spark");
			subParent.addChild(cg_cg_spark_branch);

			
			subSectParent = cg_cg_spark_branch;
			
			
			SootOption cg_Spark_General_Options_branch = new SootOption("Spark General Options");

			subSectParent.addChild(cg_Spark_General_Options_branch);
			
			SootOption cg_Spark_Pointer_Assignment_Graph_Building_Options_branch = new SootOption("Spark Pointer Assignment Graph Building Options");

			subSectParent.addChild(cg_Spark_Pointer_Assignment_Graph_Building_Options_branch);
			
			SootOption cg_Spark_Pointer_Assignment_Graph_Simplification_Options_branch = new SootOption("Spark Pointer Assignment Graph Simplification Options");

			subSectParent.addChild(cg_Spark_Pointer_Assignment_Graph_Simplification_Options_branch);
			
			SootOption cg_Spark_Points_To_Set_Flowing_Options_branch = new SootOption("Spark Points-To Set Flowing Options");

			subSectParent.addChild(cg_Spark_Points_To_Set_Flowing_Options_branch);
			
			SootOption cg_Spark_Output_Options_branch = new SootOption("Spark Output Options");

			subSectParent.addChild(cg_Spark_Output_Options_branch);
			
			//Whole Shimple Transformation Pack
			SootOption wstp_branch = new SootOption("Whole Shimple Transformation Pack");
			parent.addChild(wstp_branch);
			subParent = wstp_branch;

			//Whole Shimple Optimization Pack
			SootOption wsop_branch = new SootOption("Whole Shimple Optimization Pack");
			parent.addChild(wsop_branch);
			subParent = wsop_branch;

			//Whole-Jimple Transformation Pack
			SootOption wjtp_branch = new SootOption("Whole-Jimple Transformation Pack");
			parent.addChild(wjtp_branch);
			subParent = wjtp_branch;

			//Whole-Jimple Optimization Pack
			SootOption wjop_branch = new SootOption("Whole-Jimple Optimization Pack");
			parent.addChild(wjop_branch);
			subParent = wjop_branch;

			SootOption wjop_wjop_smb_branch = new SootOption("Static Method Binding");
			subParent.addChild(wjop_wjop_smb_branch);

			
			subSectParent = wjop_wjop_smb_branch;
			
			
			SootOption wjop_wjop_si_branch = new SootOption("Static Inlining");
			subParent.addChild(wjop_wjop_si_branch);

			
			subSectParent = wjop_wjop_si_branch;
			
			
			//Whole Jimple Annotation Pack
			SootOption wjap_branch = new SootOption("Whole Jimple Annotation Pack");
			parent.addChild(wjap_branch);
			subParent = wjap_branch;

			SootOption wjap_wjap_ra_branch = new SootOption("Rectangular Array Finder");
			subParent.addChild(wjap_wjap_ra_branch);

			
			subSectParent = wjap_wjap_ra_branch;
			
			
			//Shimple Transformation Pack
			SootOption stp_branch = new SootOption("Shimple Transformation Pack");
			parent.addChild(stp_branch);
			subParent = stp_branch;

			//Shimple Optimization Pack
			SootOption sop_branch = new SootOption("Shimple Optimization Pack");
			parent.addChild(sop_branch);
			subParent = sop_branch;

			//Jimple Transformations Pack
			SootOption jtp_branch = new SootOption("Jimple Transformations Pack");
			parent.addChild(jtp_branch);
			subParent = jtp_branch;

			//Jimple Optimizations Pack
			SootOption jop_branch = new SootOption("Jimple Optimizations Pack");
			parent.addChild(jop_branch);
			subParent = jop_branch;

			SootOption jop_jop_cse_branch = new SootOption("Common Subexpression Elimination");
			subParent.addChild(jop_jop_cse_branch);

			
			subSectParent = jop_jop_cse_branch;
			
			
			SootOption jop_jop_bcm_branch = new SootOption("Busy Code Motion");
			subParent.addChild(jop_jop_bcm_branch);

			
			subSectParent = jop_jop_bcm_branch;
			
			
			SootOption jop_jop_lcm_branch = new SootOption("Lazy Code Motion");
			subParent.addChild(jop_jop_lcm_branch);

			
			subSectParent = jop_jop_lcm_branch;
			
			
			SootOption jop_jop_cp_branch = new SootOption("Copy Propogator");
			subParent.addChild(jop_jop_cp_branch);

			
			subSectParent = jop_jop_cp_branch;
			
			
			SootOption jop_jop_cpf_branch = new SootOption("Constant Propagator and Folder");
			subParent.addChild(jop_jop_cpf_branch);

			
			subSectParent = jop_jop_cpf_branch;
			
			
			SootOption jop_jop_cbf_branch = new SootOption("Conditional Branch Folder");
			subParent.addChild(jop_jop_cbf_branch);

			
			subSectParent = jop_jop_cbf_branch;
			
			
			SootOption jop_jop_dae_branch = new SootOption("Dead Assignment Eliminator");
			subParent.addChild(jop_jop_dae_branch);

			
			subSectParent = jop_jop_dae_branch;
			
			
			SootOption jop_jop_uce1_branch = new SootOption("Unreachable Code Eliminator 1");
			subParent.addChild(jop_jop_uce1_branch);

			
			subSectParent = jop_jop_uce1_branch;
			
			
			SootOption jop_jop_uce2_branch = new SootOption("Unreachable Code Eliminator 2");
			subParent.addChild(jop_jop_uce2_branch);

			
			subSectParent = jop_jop_uce2_branch;
			
			
			SootOption jop_jop_ubf1_branch = new SootOption("Unconditional Branch Folder 1");
			subParent.addChild(jop_jop_ubf1_branch);

			
			subSectParent = jop_jop_ubf1_branch;
			
			
			SootOption jop_jop_ubf2_branch = new SootOption("Unconditional Branch Folder 2");
			subParent.addChild(jop_jop_ubf2_branch);

			
			subSectParent = jop_jop_ubf2_branch;
			
			
			SootOption jop_jop_ule_branch = new SootOption("Unused Local Eliminator");
			subParent.addChild(jop_jop_ule_branch);

			
			subSectParent = jop_jop_ule_branch;
			
			
			//Jimple Annotation Pack
			SootOption jap_branch = new SootOption("Jimple Annotation Pack");
			parent.addChild(jap_branch);
			subParent = jap_branch;

			SootOption jap_jap_npc_branch = new SootOption("Null Pointer Check Options");
			subParent.addChild(jap_jap_npc_branch);

			
			subSectParent = jap_jap_npc_branch;
			
			
			SootOption jap_jap_abc_branch = new SootOption("Array Bound Check Options");
			subParent.addChild(jap_jap_abc_branch);

			
			subSectParent = jap_jap_abc_branch;
			
			
			SootOption jap_jap_profiling_branch = new SootOption("Profiling Generator");
			subParent.addChild(jap_jap_profiling_branch);

			
			subSectParent = jap_jap_profiling_branch;
			
			
			SootOption jap_jap_sea_branch = new SootOption("Side effect tagger");
			subParent.addChild(jap_jap_sea_branch);

			
			subSectParent = jap_jap_sea_branch;
			
			
			SootOption jap_jap_fieldrw_branch = new SootOption("Field Read/Write Tagger");
			subParent.addChild(jap_jap_fieldrw_branch);

			
			subSectParent = jap_jap_fieldrw_branch;
			
			
			//Grimp Body Creation
			SootOption gb_branch = new SootOption("Grimp Body Creation");
			parent.addChild(gb_branch);
			subParent = gb_branch;

			SootOption gb_gb_a1_branch = new SootOption("Aggregator 1");
			subParent.addChild(gb_gb_a1_branch);

			
			subSectParent = gb_gb_a1_branch;
			
			
			SootOption gb_gb_cf_branch = new SootOption("Constructor Folder");
			subParent.addChild(gb_gb_cf_branch);

			
			subSectParent = gb_gb_cf_branch;
			
			
			SootOption gb_gb_a2_branch = new SootOption("Aggregator 2");
			subParent.addChild(gb_gb_a2_branch);

			
			subSectParent = gb_gb_a2_branch;
			
			
			SootOption gb_gb_ule_branch = new SootOption("Unused Local Eliminator");
			subParent.addChild(gb_gb_ule_branch);

			
			subSectParent = gb_gb_ule_branch;
			
			
			//Grimp Optimization Pack
			SootOption gop_branch = new SootOption("Grimp Optimization Pack");
			parent.addChild(gop_branch);
			subParent = gop_branch;

			//Baf Body Creation
			SootOption bb_branch = new SootOption("Baf Body Creation");
			parent.addChild(bb_branch);
			subParent = bb_branch;

			SootOption bb_bb_lso_branch = new SootOption("Load Store Optimizer");
			subParent.addChild(bb_bb_lso_branch);

			
			subSectParent = bb_bb_lso_branch;
			
			
			SootOption bb_bb_pho_branch = new SootOption("Peephole Optimizer");
			subParent.addChild(bb_bb_pho_branch);

			
			subSectParent = bb_bb_pho_branch;
			
			
			SootOption bb_bb_ule_branch = new SootOption("Unused Local Eliminator");
			subParent.addChild(bb_bb_ule_branch);

			
			subSectParent = bb_bb_ule_branch;
			
			
			SootOption bb_bb_lp_branch = new SootOption("Local Packer");
			subParent.addChild(bb_bb_lp_branch);

			
			subSectParent = bb_bb_lp_branch;
			
			
			//Baf Optimization Pack
			SootOption bop_branch = new SootOption("Baf Optimization Pack");
			parent.addChild(bop_branch);
			subParent = bop_branch;

			//Tag
			SootOption tag_branch = new SootOption("Tag");
			parent.addChild(tag_branch);
			subParent = tag_branch;

			SootOption tag_tag_ln_branch = new SootOption("Line Number Tag Aggregator");
			subParent.addChild(tag_tag_ln_branch);

			
			subSectParent = tag_tag_ln_branch;
			
			
			SootOption tag_tag_an_branch = new SootOption("Array Bounds and Null Pointer Check Tag Aggregator");
			subParent.addChild(tag_tag_an_branch);

			
			subSectParent = tag_tag_an_branch;
			
			
			SootOption tag_tag_dep_branch = new SootOption("Dependence Tag Aggregator");
			subParent.addChild(tag_tag_dep_branch);

			
			subSectParent = tag_tag_dep_branch;
			
			
			SootOption tag_tag_fieldrw_branch = new SootOption("Field Read/Write Tag Aggregator");
			subParent.addChild(tag_tag_fieldrw_branch);

			
			subSectParent = tag_tag_fieldrw_branch;
			
			
		SootOption Single_File_Mode_Options_branch = new SootOption("Single File Mode Options");
		root.addChild(Single_File_Mode_Options_branch);
		parent = Single_File_Mode_Options_branch;		
		
		SootOption Application_Mode_Options_branch = new SootOption("Application Mode Options");
		root.addChild(Application_Mode_Options_branch);
		parent = Application_Mode_Options_branch;		
		
		SootOption Input_Attribute_Options_branch = new SootOption("Input Attribute Options");
		root.addChild(Input_Attribute_Options_branch);
		parent = Input_Attribute_Options_branch;		
		
		SootOption Annotation_Options_branch = new SootOption("Annotation Options");
		root.addChild(Annotation_Options_branch);
		parent = Annotation_Options_branch;		
		
		SootOption Miscellaneous_Options_branch = new SootOption("Miscellaneous Options");
		root.addChild(Miscellaneous_Options_branch);
		parent = Miscellaneous_Options_branch;		
		
		return root;
	
	}


	private BooleanOptionWidget General_Optionshelp_widget;
	
	private void setGeneral_Optionshelp_widget(BooleanOptionWidget widget) {
		General_Optionshelp_widget = widget;
	}
	
	public BooleanOptionWidget getGeneral_Optionshelp_widget() {
		return General_Optionshelp_widget;
	}	
	
	private BooleanOptionWidget General_Optionsversion_widget;
	
	private void setGeneral_Optionsversion_widget(BooleanOptionWidget widget) {
		General_Optionsversion_widget = widget;
	}
	
	public BooleanOptionWidget getGeneral_Optionsversion_widget() {
		return General_Optionsversion_widget;
	}	
	
	private BooleanOptionWidget General_Optionsverbose_widget;
	
	private void setGeneral_Optionsverbose_widget(BooleanOptionWidget widget) {
		General_Optionsverbose_widget = widget;
	}
	
	public BooleanOptionWidget getGeneral_Optionsverbose_widget() {
		return General_Optionsverbose_widget;
	}	
	
	private BooleanOptionWidget General_Optionsapp_widget;
	
	private void setGeneral_Optionsapp_widget(BooleanOptionWidget widget) {
		General_Optionsapp_widget = widget;
	}
	
	public BooleanOptionWidget getGeneral_Optionsapp_widget() {
		return General_Optionsapp_widget;
	}	
	
	private BooleanOptionWidget General_Optionswhole_program_widget;
	
	private void setGeneral_Optionswhole_program_widget(BooleanOptionWidget widget) {
		General_Optionswhole_program_widget = widget;
	}
	
	public BooleanOptionWidget getGeneral_Optionswhole_program_widget() {
		return General_Optionswhole_program_widget;
	}	
	
	private BooleanOptionWidget Input_Optionsallow_phantom_refs_widget;
	
	private void setInput_Optionsallow_phantom_refs_widget(BooleanOptionWidget widget) {
		Input_Optionsallow_phantom_refs_widget = widget;
	}
	
	public BooleanOptionWidget getInput_Optionsallow_phantom_refs_widget() {
		return Input_Optionsallow_phantom_refs_widget;
	}	
	
	
	private StringOptionWidget Input_Optionssoot_classpath_widget;
	
	private void setInput_Optionssoot_classpath_widget(StringOptionWidget widget) {
		Input_Optionssoot_classpath_widget = widget;
	}
	
	public StringOptionWidget getInput_Optionssoot_classpath_widget() {
		return Input_Optionssoot_classpath_widget;
	}
	
	
	
	private MultiOptionWidget Input_Optionssrc_prec_widget;
	
	private void setInput_Optionssrc_prec_widget(MultiOptionWidget widget) {
		Input_Optionssrc_prec_widget = widget;
	}
	
	public MultiOptionWidget getInput_Optionssrc_prec_widget() {
		return Input_Optionssrc_prec_widget;
	}	
	
	
	private BooleanOptionWidget Output_Optionsvia_grimp_widget;
	
	private void setOutput_Optionsvia_grimp_widget(BooleanOptionWidget widget) {
		Output_Optionsvia_grimp_widget = widget;
	}
	
	public BooleanOptionWidget getOutput_Optionsvia_grimp_widget() {
		return Output_Optionsvia_grimp_widget;
	}	
	
	private BooleanOptionWidget Output_Optionsxml_attributes_widget;
	
	private void setOutput_Optionsxml_attributes_widget(BooleanOptionWidget widget) {
		Output_Optionsxml_attributes_widget = widget;
	}
	
	public BooleanOptionWidget getOutput_Optionsxml_attributes_widget() {
		return Output_Optionsxml_attributes_widget;
	}	
	
	
	private StringOptionWidget Output_Optionsoutput_dir_widget;
	
	private void setOutput_Optionsoutput_dir_widget(StringOptionWidget widget) {
		Output_Optionsoutput_dir_widget = widget;
	}
	
	public StringOptionWidget getOutput_Optionsoutput_dir_widget() {
		return Output_Optionsoutput_dir_widget;
	}
	
	
	
	private MultiOptionWidget Output_Optionsoutput_format_widget;
	
	private void setOutput_Optionsoutput_format_widget(MultiOptionWidget widget) {
		Output_Optionsoutput_format_widget = widget;
	}
	
	public MultiOptionWidget getOutput_Optionsoutput_format_widget() {
		return Output_Optionsoutput_format_widget;
	}	
	
	
	private BooleanOptionWidget Processing_Optionsoptimize_widget;
	
	private void setProcessing_Optionsoptimize_widget(BooleanOptionWidget widget) {
		Processing_Optionsoptimize_widget = widget;
	}
	
	public BooleanOptionWidget getProcessing_Optionsoptimize_widget() {
		return Processing_Optionsoptimize_widget;
	}	
	
	private BooleanOptionWidget Processing_Optionswhole_optimize_widget;
	
	private void setProcessing_Optionswhole_optimize_widget(BooleanOptionWidget widget) {
		Processing_Optionswhole_optimize_widget = widget;
	}
	
	public BooleanOptionWidget getProcessing_Optionswhole_optimize_widget() {
		return Processing_Optionswhole_optimize_widget;
	}	
	
	private BooleanOptionWidget Processing_Optionsvia_shimple_widget;
	
	private void setProcessing_Optionsvia_shimple_widget(BooleanOptionWidget widget) {
		Processing_Optionsvia_shimple_widget = widget;
	}
	
	public BooleanOptionWidget getProcessing_Optionsvia_shimple_widget() {
		return Processing_Optionsvia_shimple_widget;
	}	
	
	private BooleanOptionWidget jbdisabled_widget;
	
	private void setjbdisabled_widget(BooleanOptionWidget widget) {
		jbdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbdisabled_widget() {
		return jbdisabled_widget;
	}	
	
	private BooleanOptionWidget jbno_splitting_widget;
	
	private void setjbno_splitting_widget(BooleanOptionWidget widget) {
		jbno_splitting_widget = widget;
	}
	
	public BooleanOptionWidget getjbno_splitting_widget() {
		return jbno_splitting_widget;
	}	
	
	private BooleanOptionWidget jbno_typing_widget;
	
	private void setjbno_typing_widget(BooleanOptionWidget widget) {
		jbno_typing_widget = widget;
	}
	
	public BooleanOptionWidget getjbno_typing_widget() {
		return jbno_typing_widget;
	}	
	
	private BooleanOptionWidget jbaggregate_all_locals_widget;
	
	private void setjbaggregate_all_locals_widget(BooleanOptionWidget widget) {
		jbaggregate_all_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbaggregate_all_locals_widget() {
		return jbaggregate_all_locals_widget;
	}	
	
	private BooleanOptionWidget jbno_aggregating_widget;
	
	private void setjbno_aggregating_widget(BooleanOptionWidget widget) {
		jbno_aggregating_widget = widget;
	}
	
	public BooleanOptionWidget getjbno_aggregating_widget() {
		return jbno_aggregating_widget;
	}	
	
	private BooleanOptionWidget jbuse_original_names_widget;
	
	private void setjbuse_original_names_widget(BooleanOptionWidget widget) {
		jbuse_original_names_widget = widget;
	}
	
	public BooleanOptionWidget getjbuse_original_names_widget() {
		return jbuse_original_names_widget;
	}	
	
	private BooleanOptionWidget jbpack_locals_widget;
	
	private void setjbpack_locals_widget(BooleanOptionWidget widget) {
		jbpack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbpack_locals_widget() {
		return jbpack_locals_widget;
	}	
	
	private BooleanOptionWidget jbno_cp_widget;
	
	private void setjbno_cp_widget(BooleanOptionWidget widget) {
		jbno_cp_widget = widget;
	}
	
	public BooleanOptionWidget getjbno_cp_widget() {
		return jbno_cp_widget;
	}	
	
	private BooleanOptionWidget jbno_nop_elimination_widget;
	
	private void setjbno_nop_elimination_widget(BooleanOptionWidget widget) {
		jbno_nop_elimination_widget = widget;
	}
	
	public BooleanOptionWidget getjbno_nop_elimination_widget() {
		return jbno_nop_elimination_widget;
	}	
	
	private BooleanOptionWidget jbno_unreachable_code_elimination_widget;
	
	private void setjbno_unreachable_code_elimination_widget(BooleanOptionWidget widget) {
		jbno_unreachable_code_elimination_widget = widget;
	}
	
	public BooleanOptionWidget getjbno_unreachable_code_elimination_widget() {
		return jbno_unreachable_code_elimination_widget;
	}	
	
	private BooleanOptionWidget jbverbatim_widget;
	
	private void setjbverbatim_widget(BooleanOptionWidget widget) {
		jbverbatim_widget = widget;
	}
	
	public BooleanOptionWidget getjbverbatim_widget() {
		return jbverbatim_widget;
	}	
	
	private BooleanOptionWidget jbjb_asvdisabled_widget;
	
	private void setjbjb_asvdisabled_widget(BooleanOptionWidget widget) {
		jbjb_asvdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_asvdisabled_widget() {
		return jbjb_asvdisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_asvonly_stack_locals_widget;
	
	private void setjbjb_asvonly_stack_locals_widget(BooleanOptionWidget widget) {
		jbjb_asvonly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_asvonly_stack_locals_widget() {
		return jbjb_asvonly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_ulpdisabled_widget;
	
	private void setjbjb_ulpdisabled_widget(BooleanOptionWidget widget) {
		jbjb_ulpdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_ulpdisabled_widget() {
		return jbjb_ulpdisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_ulpunsplit_original_locals_widget;
	
	private void setjbjb_ulpunsplit_original_locals_widget(BooleanOptionWidget widget) {
		jbjb_ulpunsplit_original_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_ulpunsplit_original_locals_widget() {
		return jbjb_ulpunsplit_original_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_lnsdisabled_widget;
	
	private void setjbjb_lnsdisabled_widget(BooleanOptionWidget widget) {
		jbjb_lnsdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lnsdisabled_widget() {
		return jbjb_lnsdisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_lnsonly_stack_locals_widget;
	
	private void setjbjb_lnsonly_stack_locals_widget(BooleanOptionWidget widget) {
		jbjb_lnsonly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lnsonly_stack_locals_widget() {
		return jbjb_lnsonly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_cpdisabled_widget;
	
	private void setjbjb_cpdisabled_widget(BooleanOptionWidget widget) {
		jbjb_cpdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_cpdisabled_widget() {
		return jbjb_cpdisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_cponly_regular_locals_widget;
	
	private void setjbjb_cponly_regular_locals_widget(BooleanOptionWidget widget) {
		jbjb_cponly_regular_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_cponly_regular_locals_widget() {
		return jbjb_cponly_regular_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_cponly_stack_locals_widget;
	
	private void setjbjb_cponly_stack_locals_widget(BooleanOptionWidget widget) {
		jbjb_cponly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_cponly_stack_locals_widget() {
		return jbjb_cponly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_daedisabled_widget;
	
	private void setjbjb_daedisabled_widget(BooleanOptionWidget widget) {
		jbjb_daedisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_daedisabled_widget() {
		return jbjb_daedisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_daeonly_stack_locals_widget;
	
	private void setjbjb_daeonly_stack_locals_widget(BooleanOptionWidget widget) {
		jbjb_daeonly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_daeonly_stack_locals_widget() {
		return jbjb_daeonly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_lsdisabled_widget;
	
	private void setjbjb_lsdisabled_widget(BooleanOptionWidget widget) {
		jbjb_lsdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lsdisabled_widget() {
		return jbjb_lsdisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_adisabled_widget;
	
	private void setjbjb_adisabled_widget(BooleanOptionWidget widget) {
		jbjb_adisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_adisabled_widget() {
		return jbjb_adisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_aonly_stack_locals_widget;
	
	private void setjbjb_aonly_stack_locals_widget(BooleanOptionWidget widget) {
		jbjb_aonly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_aonly_stack_locals_widget() {
		return jbjb_aonly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_uledisabled_widget;
	
	private void setjbjb_uledisabled_widget(BooleanOptionWidget widget) {
		jbjb_uledisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_uledisabled_widget() {
		return jbjb_uledisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_trdisabled_widget;
	
	private void setjbjb_trdisabled_widget(BooleanOptionWidget widget) {
		jbjb_trdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_trdisabled_widget() {
		return jbjb_trdisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_cp_uledisabled_widget;
	
	private void setjbjb_cp_uledisabled_widget(BooleanOptionWidget widget) {
		jbjb_cp_uledisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_cp_uledisabled_widget() {
		return jbjb_cp_uledisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_lpdisabled_widget;
	
	private void setjbjb_lpdisabled_widget(BooleanOptionWidget widget) {
		jbjb_lpdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lpdisabled_widget() {
		return jbjb_lpdisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_lpunsplit_original_locals_widget;
	
	private void setjbjb_lpunsplit_original_locals_widget(BooleanOptionWidget widget) {
		jbjb_lpunsplit_original_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lpunsplit_original_locals_widget() {
		return jbjb_lpunsplit_original_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_nedisabled_widget;
	
	private void setjbjb_nedisabled_widget(BooleanOptionWidget widget) {
		jbjb_nedisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_nedisabled_widget() {
		return jbjb_nedisabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_ucedisabled_widget;
	
	private void setjbjb_ucedisabled_widget(BooleanOptionWidget widget) {
		jbjb_ucedisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_ucedisabled_widget() {
		return jbjb_ucedisabled_widget;
	}	
	
	private BooleanOptionWidget cgdisabled_widget;
	
	private void setcgdisabled_widget(BooleanOptionWidget widget) {
		cgdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getcgdisabled_widget() {
		return cgdisabled_widget;
	}	
	
	private BooleanOptionWidget cgcg_oldchadisabled_widget;
	
	private void setcgcg_oldchadisabled_widget(BooleanOptionWidget widget) {
		cgcg_oldchadisabled_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_oldchadisabled_widget() {
		return cgcg_oldchadisabled_widget;
	}	
	
	private BooleanOptionWidget cgcg_vtadisabled_widget;
	
	private void setcgcg_vtadisabled_widget(BooleanOptionWidget widget) {
		cgcg_vtadisabled_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_vtadisabled_widget() {
		return cgcg_vtadisabled_widget;
	}	
	
	
	private StringOptionWidget cgcg_vtapasses_widget;
	
	private void setcgcg_vtapasses_widget(StringOptionWidget widget) {
		cgcg_vtapasses_widget = widget;
	}
	
	public StringOptionWidget getcgcg_vtapasses_widget() {
		return cgcg_vtapasses_widget;
	}
	
	
	private BooleanOptionWidget cgcg_chadisabled_widget;
	
	private void setcgcg_chadisabled_widget(BooleanOptionWidget widget) {
		cgcg_chadisabled_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_chadisabled_widget() {
		return cgcg_chadisabled_widget;
	}	
	
	private BooleanOptionWidget cgcg_chaverbose_widget;
	
	private void setcgcg_chaverbose_widget(BooleanOptionWidget widget) {
		cgcg_chaverbose_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_chaverbose_widget() {
		return cgcg_chaverbose_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkdisabled_widget;
	
	private void setcgcg_sparkdisabled_widget(BooleanOptionWidget widget) {
		cgcg_sparkdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkdisabled_widget() {
		return cgcg_sparkdisabled_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkverbose_widget;
	
	private void setcgcg_sparkverbose_widget(BooleanOptionWidget widget) {
		cgcg_sparkverbose_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkverbose_widget() {
		return cgcg_sparkverbose_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkignore_types_widget;
	
	private void setcgcg_sparkignore_types_widget(BooleanOptionWidget widget) {
		cgcg_sparkignore_types_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkignore_types_widget() {
		return cgcg_sparkignore_types_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkforce_gc_widget;
	
	private void setcgcg_sparkforce_gc_widget(BooleanOptionWidget widget) {
		cgcg_sparkforce_gc_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkforce_gc_widget() {
		return cgcg_sparkforce_gc_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkpre_jimplify_widget;
	
	private void setcgcg_sparkpre_jimplify_widget(BooleanOptionWidget widget) {
		cgcg_sparkpre_jimplify_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkpre_jimplify_widget() {
		return cgcg_sparkpre_jimplify_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkvta_widget;
	
	private void setcgcg_sparkvta_widget(BooleanOptionWidget widget) {
		cgcg_sparkvta_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkvta_widget() {
		return cgcg_sparkvta_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkrta_widget;
	
	private void setcgcg_sparkrta_widget(BooleanOptionWidget widget) {
		cgcg_sparkrta_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkrta_widget() {
		return cgcg_sparkrta_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkfield_based_widget;
	
	private void setcgcg_sparkfield_based_widget(BooleanOptionWidget widget) {
		cgcg_sparkfield_based_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkfield_based_widget() {
		return cgcg_sparkfield_based_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparktypes_for_sites_widget;
	
	private void setcgcg_sparktypes_for_sites_widget(BooleanOptionWidget widget) {
		cgcg_sparktypes_for_sites_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparktypes_for_sites_widget() {
		return cgcg_sparktypes_for_sites_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkmerge_stringbuffer_widget;
	
	private void setcgcg_sparkmerge_stringbuffer_widget(BooleanOptionWidget widget) {
		cgcg_sparkmerge_stringbuffer_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkmerge_stringbuffer_widget() {
		return cgcg_sparkmerge_stringbuffer_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparksimulate_natives_widget;
	
	private void setcgcg_sparksimulate_natives_widget(BooleanOptionWidget widget) {
		cgcg_sparksimulate_natives_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparksimulate_natives_widget() {
		return cgcg_sparksimulate_natives_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparksimple_edges_bidirectional_widget;
	
	private void setcgcg_sparksimple_edges_bidirectional_widget(BooleanOptionWidget widget) {
		cgcg_sparksimple_edges_bidirectional_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparksimple_edges_bidirectional_widget() {
		return cgcg_sparksimple_edges_bidirectional_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkon_fly_cg_widget;
	
	private void setcgcg_sparkon_fly_cg_widget(BooleanOptionWidget widget) {
		cgcg_sparkon_fly_cg_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkon_fly_cg_widget() {
		return cgcg_sparkon_fly_cg_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkparms_as_fields_widget;
	
	private void setcgcg_sparkparms_as_fields_widget(BooleanOptionWidget widget) {
		cgcg_sparkparms_as_fields_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkparms_as_fields_widget() {
		return cgcg_sparkparms_as_fields_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkreturns_as_fields_widget;
	
	private void setcgcg_sparkreturns_as_fields_widget(BooleanOptionWidget widget) {
		cgcg_sparkreturns_as_fields_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkreturns_as_fields_widget() {
		return cgcg_sparkreturns_as_fields_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparksimplify_offline_widget;
	
	private void setcgcg_sparksimplify_offline_widget(BooleanOptionWidget widget) {
		cgcg_sparksimplify_offline_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparksimplify_offline_widget() {
		return cgcg_sparksimplify_offline_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparksimplify_sccs_widget;
	
	private void setcgcg_sparksimplify_sccs_widget(BooleanOptionWidget widget) {
		cgcg_sparksimplify_sccs_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparksimplify_sccs_widget() {
		return cgcg_sparksimplify_sccs_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkignore_types_for_sccs_widget;
	
	private void setcgcg_sparkignore_types_for_sccs_widget(BooleanOptionWidget widget) {
		cgcg_sparkignore_types_for_sccs_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkignore_types_for_sccs_widget() {
		return cgcg_sparkignore_types_for_sccs_widget;
	}	
	
	
	private MultiOptionWidget cgcg_sparkpropagator_widget;
	
	private void setcgcg_sparkpropagator_widget(MultiOptionWidget widget) {
		cgcg_sparkpropagator_widget = widget;
	}
	
	public MultiOptionWidget getcgcg_sparkpropagator_widget() {
		return cgcg_sparkpropagator_widget;
	}	
	
	
	
	private MultiOptionWidget cgcg_sparkset_impl_widget;
	
	private void setcgcg_sparkset_impl_widget(MultiOptionWidget widget) {
		cgcg_sparkset_impl_widget = widget;
	}
	
	public MultiOptionWidget getcgcg_sparkset_impl_widget() {
		return cgcg_sparkset_impl_widget;
	}	
	
	
	
	private MultiOptionWidget cgcg_sparkdouble_set_old_widget;
	
	private void setcgcg_sparkdouble_set_old_widget(MultiOptionWidget widget) {
		cgcg_sparkdouble_set_old_widget = widget;
	}
	
	public MultiOptionWidget getcgcg_sparkdouble_set_old_widget() {
		return cgcg_sparkdouble_set_old_widget;
	}	
	
	
	
	private MultiOptionWidget cgcg_sparkdouble_set_new_widget;
	
	private void setcgcg_sparkdouble_set_new_widget(MultiOptionWidget widget) {
		cgcg_sparkdouble_set_new_widget = widget;
	}
	
	public MultiOptionWidget getcgcg_sparkdouble_set_new_widget() {
		return cgcg_sparkdouble_set_new_widget;
	}	
	
	
	private BooleanOptionWidget cgcg_sparkdump_html_widget;
	
	private void setcgcg_sparkdump_html_widget(BooleanOptionWidget widget) {
		cgcg_sparkdump_html_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkdump_html_widget() {
		return cgcg_sparkdump_html_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkdump_pag_widget;
	
	private void setcgcg_sparkdump_pag_widget(BooleanOptionWidget widget) {
		cgcg_sparkdump_pag_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkdump_pag_widget() {
		return cgcg_sparkdump_pag_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkdump_solution_widget;
	
	private void setcgcg_sparkdump_solution_widget(BooleanOptionWidget widget) {
		cgcg_sparkdump_solution_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkdump_solution_widget() {
		return cgcg_sparkdump_solution_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparktopo_sort_widget;
	
	private void setcgcg_sparktopo_sort_widget(BooleanOptionWidget widget) {
		cgcg_sparktopo_sort_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparktopo_sort_widget() {
		return cgcg_sparktopo_sort_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkdump_types_widget;
	
	private void setcgcg_sparkdump_types_widget(BooleanOptionWidget widget) {
		cgcg_sparkdump_types_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkdump_types_widget() {
		return cgcg_sparkdump_types_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkclass_method_var_widget;
	
	private void setcgcg_sparkclass_method_var_widget(BooleanOptionWidget widget) {
		cgcg_sparkclass_method_var_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkclass_method_var_widget() {
		return cgcg_sparkclass_method_var_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkdump_answer_widget;
	
	private void setcgcg_sparkdump_answer_widget(BooleanOptionWidget widget) {
		cgcg_sparkdump_answer_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkdump_answer_widget() {
		return cgcg_sparkdump_answer_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparktrim_invoke_graph_widget;
	
	private void setcgcg_sparktrim_invoke_graph_widget(BooleanOptionWidget widget) {
		cgcg_sparktrim_invoke_graph_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparktrim_invoke_graph_widget() {
		return cgcg_sparktrim_invoke_graph_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkadd_tags_widget;
	
	private void setcgcg_sparkadd_tags_widget(BooleanOptionWidget widget) {
		cgcg_sparkadd_tags_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkadd_tags_widget() {
		return cgcg_sparkadd_tags_widget;
	}	
	
	private BooleanOptionWidget wstpdisabled_widget;
	
	private void setwstpdisabled_widget(BooleanOptionWidget widget) {
		wstpdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getwstpdisabled_widget() {
		return wstpdisabled_widget;
	}	
	
	private BooleanOptionWidget wsopdisabled_widget;
	
	private void setwsopdisabled_widget(BooleanOptionWidget widget) {
		wsopdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getwsopdisabled_widget() {
		return wsopdisabled_widget;
	}	
	
	private BooleanOptionWidget wjtpdisabled_widget;
	
	private void setwjtpdisabled_widget(BooleanOptionWidget widget) {
		wjtpdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjtpdisabled_widget() {
		return wjtpdisabled_widget;
	}	
	
	private BooleanOptionWidget wjopdisabled_widget;
	
	private void setwjopdisabled_widget(BooleanOptionWidget widget) {
		wjopdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjopdisabled_widget() {
		return wjopdisabled_widget;
	}	
	
	private BooleanOptionWidget wjopwjop_smbdisabled_widget;
	
	private void setwjopwjop_smbdisabled_widget(BooleanOptionWidget widget) {
		wjopwjop_smbdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjopwjop_smbdisabled_widget() {
		return wjopwjop_smbdisabled_widget;
	}	
	
	private BooleanOptionWidget wjopwjop_smbinsert_null_checks_widget;
	
	private void setwjopwjop_smbinsert_null_checks_widget(BooleanOptionWidget widget) {
		wjopwjop_smbinsert_null_checks_widget = widget;
	}
	
	public BooleanOptionWidget getwjopwjop_smbinsert_null_checks_widget() {
		return wjopwjop_smbinsert_null_checks_widget;
	}	
	
	private BooleanOptionWidget wjopwjop_smbinsert_redundant_casts_widget;
	
	private void setwjopwjop_smbinsert_redundant_casts_widget(BooleanOptionWidget widget) {
		wjopwjop_smbinsert_redundant_casts_widget = widget;
	}
	
	public BooleanOptionWidget getwjopwjop_smbinsert_redundant_casts_widget() {
		return wjopwjop_smbinsert_redundant_casts_widget;
	}	
	
	
	private MultiOptionWidget wjopwjop_smballowed_modifier_changes_widget;
	
	private void setwjopwjop_smballowed_modifier_changes_widget(MultiOptionWidget widget) {
		wjopwjop_smballowed_modifier_changes_widget = widget;
	}
	
	public MultiOptionWidget getwjopwjop_smballowed_modifier_changes_widget() {
		return wjopwjop_smballowed_modifier_changes_widget;
	}	
	
	
	private BooleanOptionWidget wjopwjop_sidisabled_widget;
	
	private void setwjopwjop_sidisabled_widget(BooleanOptionWidget widget) {
		wjopwjop_sidisabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjopwjop_sidisabled_widget() {
		return wjopwjop_sidisabled_widget;
	}	
	
	private BooleanOptionWidget wjopwjop_siinsert_null_checks_widget;
	
	private void setwjopwjop_siinsert_null_checks_widget(BooleanOptionWidget widget) {
		wjopwjop_siinsert_null_checks_widget = widget;
	}
	
	public BooleanOptionWidget getwjopwjop_siinsert_null_checks_widget() {
		return wjopwjop_siinsert_null_checks_widget;
	}	
	
	private BooleanOptionWidget wjopwjop_siinsert_redundant_casts_widget;
	
	private void setwjopwjop_siinsert_redundant_casts_widget(BooleanOptionWidget widget) {
		wjopwjop_siinsert_redundant_casts_widget = widget;
	}
	
	public BooleanOptionWidget getwjopwjop_siinsert_redundant_casts_widget() {
		return wjopwjop_siinsert_redundant_casts_widget;
	}	
	
	
	private StringOptionWidget wjopwjop_siexpansion_factor_widget;
	
	private void setwjopwjop_siexpansion_factor_widget(StringOptionWidget widget) {
		wjopwjop_siexpansion_factor_widget = widget;
	}
	
	public StringOptionWidget getwjopwjop_siexpansion_factor_widget() {
		return wjopwjop_siexpansion_factor_widget;
	}
	
	
	
	private StringOptionWidget wjopwjop_simax_container_size_widget;
	
	private void setwjopwjop_simax_container_size_widget(StringOptionWidget widget) {
		wjopwjop_simax_container_size_widget = widget;
	}
	
	public StringOptionWidget getwjopwjop_simax_container_size_widget() {
		return wjopwjop_simax_container_size_widget;
	}
	
	
	
	private StringOptionWidget wjopwjop_simax_inlinee_size_widget;
	
	private void setwjopwjop_simax_inlinee_size_widget(StringOptionWidget widget) {
		wjopwjop_simax_inlinee_size_widget = widget;
	}
	
	public StringOptionWidget getwjopwjop_simax_inlinee_size_widget() {
		return wjopwjop_simax_inlinee_size_widget;
	}
	
	
	
	private MultiOptionWidget wjopwjop_siallowed_modifier_changes_widget;
	
	private void setwjopwjop_siallowed_modifier_changes_widget(MultiOptionWidget widget) {
		wjopwjop_siallowed_modifier_changes_widget = widget;
	}
	
	public MultiOptionWidget getwjopwjop_siallowed_modifier_changes_widget() {
		return wjopwjop_siallowed_modifier_changes_widget;
	}	
	
	
	private BooleanOptionWidget wjapdisabled_widget;
	
	private void setwjapdisabled_widget(BooleanOptionWidget widget) {
		wjapdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjapdisabled_widget() {
		return wjapdisabled_widget;
	}	
	
	private BooleanOptionWidget wjapwjap_radisabled_widget;
	
	private void setwjapwjap_radisabled_widget(BooleanOptionWidget widget) {
		wjapwjap_radisabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjapwjap_radisabled_widget() {
		return wjapwjap_radisabled_widget;
	}	
	
	private BooleanOptionWidget stpdisabled_widget;
	
	private void setstpdisabled_widget(BooleanOptionWidget widget) {
		stpdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getstpdisabled_widget() {
		return stpdisabled_widget;
	}	
	
	private BooleanOptionWidget sopdisabled_widget;
	
	private void setsopdisabled_widget(BooleanOptionWidget widget) {
		sopdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getsopdisabled_widget() {
		return sopdisabled_widget;
	}	
	
	private BooleanOptionWidget jtpdisabled_widget;
	
	private void setjtpdisabled_widget(BooleanOptionWidget widget) {
		jtpdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjtpdisabled_widget() {
		return jtpdisabled_widget;
	}	
	
	private BooleanOptionWidget jopdisabled_widget;
	
	private void setjopdisabled_widget(BooleanOptionWidget widget) {
		jopdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopdisabled_widget() {
		return jopdisabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_csedisabled_widget;
	
	private void setjopjop_csedisabled_widget(BooleanOptionWidget widget) {
		jopjop_csedisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_csedisabled_widget() {
		return jopjop_csedisabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_csenaive_side_effect_widget;
	
	private void setjopjop_csenaive_side_effect_widget(BooleanOptionWidget widget) {
		jopjop_csenaive_side_effect_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_csenaive_side_effect_widget() {
		return jopjop_csenaive_side_effect_widget;
	}	
	
	private BooleanOptionWidget jopjop_bcmdisabled_widget;
	
	private void setjopjop_bcmdisabled_widget(BooleanOptionWidget widget) {
		jopjop_bcmdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_bcmdisabled_widget() {
		return jopjop_bcmdisabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_bcmnaive_side_effect_widget;
	
	private void setjopjop_bcmnaive_side_effect_widget(BooleanOptionWidget widget) {
		jopjop_bcmnaive_side_effect_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_bcmnaive_side_effect_widget() {
		return jopjop_bcmnaive_side_effect_widget;
	}	
	
	private BooleanOptionWidget jopjop_lcmdisabled_widget;
	
	private void setjopjop_lcmdisabled_widget(BooleanOptionWidget widget) {
		jopjop_lcmdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_lcmdisabled_widget() {
		return jopjop_lcmdisabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_lcmunroll_widget;
	
	private void setjopjop_lcmunroll_widget(BooleanOptionWidget widget) {
		jopjop_lcmunroll_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_lcmunroll_widget() {
		return jopjop_lcmunroll_widget;
	}	
	
	private BooleanOptionWidget jopjop_lcmnaive_side_effect_widget;
	
	private void setjopjop_lcmnaive_side_effect_widget(BooleanOptionWidget widget) {
		jopjop_lcmnaive_side_effect_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_lcmnaive_side_effect_widget() {
		return jopjop_lcmnaive_side_effect_widget;
	}	
	
	
	private MultiOptionWidget jopjop_lcmsafe_widget;
	
	private void setjopjop_lcmsafe_widget(MultiOptionWidget widget) {
		jopjop_lcmsafe_widget = widget;
	}
	
	public MultiOptionWidget getjopjop_lcmsafe_widget() {
		return jopjop_lcmsafe_widget;
	}	
	
	
	private BooleanOptionWidget jopjop_cpdisabled_widget;
	
	private void setjopjop_cpdisabled_widget(BooleanOptionWidget widget) {
		jopjop_cpdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_cpdisabled_widget() {
		return jopjop_cpdisabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_cponly_regular_locals_widget;
	
	private void setjopjop_cponly_regular_locals_widget(BooleanOptionWidget widget) {
		jopjop_cponly_regular_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_cponly_regular_locals_widget() {
		return jopjop_cponly_regular_locals_widget;
	}	
	
	private BooleanOptionWidget jopjop_cponly_stack_locals_widget;
	
	private void setjopjop_cponly_stack_locals_widget(BooleanOptionWidget widget) {
		jopjop_cponly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_cponly_stack_locals_widget() {
		return jopjop_cponly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jopjop_cpfdisabled_widget;
	
	private void setjopjop_cpfdisabled_widget(BooleanOptionWidget widget) {
		jopjop_cpfdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_cpfdisabled_widget() {
		return jopjop_cpfdisabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_cbfdisabled_widget;
	
	private void setjopjop_cbfdisabled_widget(BooleanOptionWidget widget) {
		jopjop_cbfdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_cbfdisabled_widget() {
		return jopjop_cbfdisabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_daedisabled_widget;
	
	private void setjopjop_daedisabled_widget(BooleanOptionWidget widget) {
		jopjop_daedisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_daedisabled_widget() {
		return jopjop_daedisabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_daeonly_stack_locals_widget;
	
	private void setjopjop_daeonly_stack_locals_widget(BooleanOptionWidget widget) {
		jopjop_daeonly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_daeonly_stack_locals_widget() {
		return jopjop_daeonly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jopjop_uce1disabled_widget;
	
	private void setjopjop_uce1disabled_widget(BooleanOptionWidget widget) {
		jopjop_uce1disabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_uce1disabled_widget() {
		return jopjop_uce1disabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_uce2disabled_widget;
	
	private void setjopjop_uce2disabled_widget(BooleanOptionWidget widget) {
		jopjop_uce2disabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_uce2disabled_widget() {
		return jopjop_uce2disabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_ubf1disabled_widget;
	
	private void setjopjop_ubf1disabled_widget(BooleanOptionWidget widget) {
		jopjop_ubf1disabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_ubf1disabled_widget() {
		return jopjop_ubf1disabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_ubf2disabled_widget;
	
	private void setjopjop_ubf2disabled_widget(BooleanOptionWidget widget) {
		jopjop_ubf2disabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_ubf2disabled_widget() {
		return jopjop_ubf2disabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_uledisabled_widget;
	
	private void setjopjop_uledisabled_widget(BooleanOptionWidget widget) {
		jopjop_uledisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_uledisabled_widget() {
		return jopjop_uledisabled_widget;
	}	
	
	private BooleanOptionWidget japdisabled_widget;
	
	private void setjapdisabled_widget(BooleanOptionWidget widget) {
		japdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapdisabled_widget() {
		return japdisabled_widget;
	}	
	
	private BooleanOptionWidget japjap_npcdisabled_widget;
	
	private void setjapjap_npcdisabled_widget(BooleanOptionWidget widget) {
		japjap_npcdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_npcdisabled_widget() {
		return japjap_npcdisabled_widget;
	}	
	
	private BooleanOptionWidget japjap_npconly_array_ref_widget;
	
	private void setjapjap_npconly_array_ref_widget(BooleanOptionWidget widget) {
		japjap_npconly_array_ref_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_npconly_array_ref_widget() {
		return japjap_npconly_array_ref_widget;
	}	
	
	private BooleanOptionWidget japjap_npcprofiling_widget;
	
	private void setjapjap_npcprofiling_widget(BooleanOptionWidget widget) {
		japjap_npcprofiling_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_npcprofiling_widget() {
		return japjap_npcprofiling_widget;
	}	
	
	private BooleanOptionWidget japjap_abcdisabled_widget;
	
	private void setjapjap_abcdisabled_widget(BooleanOptionWidget widget) {
		japjap_abcdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_abcdisabled_widget() {
		return japjap_abcdisabled_widget;
	}	
	
	private BooleanOptionWidget japjap_abcwith_all_widget;
	
	private void setjapjap_abcwith_all_widget(BooleanOptionWidget widget) {
		japjap_abcwith_all_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_abcwith_all_widget() {
		return japjap_abcwith_all_widget;
	}	
	
	private BooleanOptionWidget japjap_abcwith_fieldref_widget;
	
	private void setjapjap_abcwith_fieldref_widget(BooleanOptionWidget widget) {
		japjap_abcwith_fieldref_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_abcwith_fieldref_widget() {
		return japjap_abcwith_fieldref_widget;
	}	
	
	private BooleanOptionWidget japjap_abcwith_arrayref_widget;
	
	private void setjapjap_abcwith_arrayref_widget(BooleanOptionWidget widget) {
		japjap_abcwith_arrayref_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_abcwith_arrayref_widget() {
		return japjap_abcwith_arrayref_widget;
	}	
	
	private BooleanOptionWidget japjap_abcwith_cse_widget;
	
	private void setjapjap_abcwith_cse_widget(BooleanOptionWidget widget) {
		japjap_abcwith_cse_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_abcwith_cse_widget() {
		return japjap_abcwith_cse_widget;
	}	
	
	private BooleanOptionWidget japjap_abcwith_classfield_widget;
	
	private void setjapjap_abcwith_classfield_widget(BooleanOptionWidget widget) {
		japjap_abcwith_classfield_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_abcwith_classfield_widget() {
		return japjap_abcwith_classfield_widget;
	}	
	
	private BooleanOptionWidget japjap_abcwith_rectarray_widget;
	
	private void setjapjap_abcwith_rectarray_widget(BooleanOptionWidget widget) {
		japjap_abcwith_rectarray_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_abcwith_rectarray_widget() {
		return japjap_abcwith_rectarray_widget;
	}	
	
	private BooleanOptionWidget japjap_abcprofiling_widget;
	
	private void setjapjap_abcprofiling_widget(BooleanOptionWidget widget) {
		japjap_abcprofiling_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_abcprofiling_widget() {
		return japjap_abcprofiling_widget;
	}	
	
	private BooleanOptionWidget japjap_profilingdisabled_widget;
	
	private void setjapjap_profilingdisabled_widget(BooleanOptionWidget widget) {
		japjap_profilingdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_profilingdisabled_widget() {
		return japjap_profilingdisabled_widget;
	}	
	
	private BooleanOptionWidget japjap_profilingnotmainentry_widget;
	
	private void setjapjap_profilingnotmainentry_widget(BooleanOptionWidget widget) {
		japjap_profilingnotmainentry_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_profilingnotmainentry_widget() {
		return japjap_profilingnotmainentry_widget;
	}	
	
	private BooleanOptionWidget japjap_seadisabled_widget;
	
	private void setjapjap_seadisabled_widget(BooleanOptionWidget widget) {
		japjap_seadisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_seadisabled_widget() {
		return japjap_seadisabled_widget;
	}	
	
	private BooleanOptionWidget japjap_seanaive_widget;
	
	private void setjapjap_seanaive_widget(BooleanOptionWidget widget) {
		japjap_seanaive_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_seanaive_widget() {
		return japjap_seanaive_widget;
	}	
	
	private BooleanOptionWidget japjap_fieldrwdisabled_widget;
	
	private void setjapjap_fieldrwdisabled_widget(BooleanOptionWidget widget) {
		japjap_fieldrwdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_fieldrwdisabled_widget() {
		return japjap_fieldrwdisabled_widget;
	}	
	
	
	private StringOptionWidget japjap_fieldrwthreshold_widget;
	
	private void setjapjap_fieldrwthreshold_widget(StringOptionWidget widget) {
		japjap_fieldrwthreshold_widget = widget;
	}
	
	public StringOptionWidget getjapjap_fieldrwthreshold_widget() {
		return japjap_fieldrwthreshold_widget;
	}
	
	
	private BooleanOptionWidget gbdisabled_widget;
	
	private void setgbdisabled_widget(BooleanOptionWidget widget) {
		gbdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbdisabled_widget() {
		return gbdisabled_widget;
	}	
	
	private BooleanOptionWidget gbgb_a1disabled_widget;
	
	private void setgbgb_a1disabled_widget(BooleanOptionWidget widget) {
		gbgb_a1disabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_a1disabled_widget() {
		return gbgb_a1disabled_widget;
	}	
	
	private BooleanOptionWidget gbgb_a1only_stack_locals_widget;
	
	private void setgbgb_a1only_stack_locals_widget(BooleanOptionWidget widget) {
		gbgb_a1only_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_a1only_stack_locals_widget() {
		return gbgb_a1only_stack_locals_widget;
	}	
	
	private BooleanOptionWidget gbgb_cfdisabled_widget;
	
	private void setgbgb_cfdisabled_widget(BooleanOptionWidget widget) {
		gbgb_cfdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_cfdisabled_widget() {
		return gbgb_cfdisabled_widget;
	}	
	
	private BooleanOptionWidget gbgb_a2disabled_widget;
	
	private void setgbgb_a2disabled_widget(BooleanOptionWidget widget) {
		gbgb_a2disabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_a2disabled_widget() {
		return gbgb_a2disabled_widget;
	}	
	
	private BooleanOptionWidget gbgb_a2only_stack_locals_widget;
	
	private void setgbgb_a2only_stack_locals_widget(BooleanOptionWidget widget) {
		gbgb_a2only_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_a2only_stack_locals_widget() {
		return gbgb_a2only_stack_locals_widget;
	}	
	
	private BooleanOptionWidget gbgb_uledisabled_widget;
	
	private void setgbgb_uledisabled_widget(BooleanOptionWidget widget) {
		gbgb_uledisabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_uledisabled_widget() {
		return gbgb_uledisabled_widget;
	}	
	
	private BooleanOptionWidget gopdisabled_widget;
	
	private void setgopdisabled_widget(BooleanOptionWidget widget) {
		gopdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getgopdisabled_widget() {
		return gopdisabled_widget;
	}	
	
	private BooleanOptionWidget bbdisabled_widget;
	
	private void setbbdisabled_widget(BooleanOptionWidget widget) {
		bbdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbdisabled_widget() {
		return bbdisabled_widget;
	}	
	
	private BooleanOptionWidget bbbb_lsodisabled_widget;
	
	private void setbbbb_lsodisabled_widget(BooleanOptionWidget widget) {
		bbbb_lsodisabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lsodisabled_widget() {
		return bbbb_lsodisabled_widget;
	}	
	
	private BooleanOptionWidget bbbb_lsodebug_widget;
	
	private void setbbbb_lsodebug_widget(BooleanOptionWidget widget) {
		bbbb_lsodebug_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lsodebug_widget() {
		return bbbb_lsodebug_widget;
	}	
	
	private BooleanOptionWidget bbbb_lsointer_widget;
	
	private void setbbbb_lsointer_widget(BooleanOptionWidget widget) {
		bbbb_lsointer_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lsointer_widget() {
		return bbbb_lsointer_widget;
	}	
	
	private BooleanOptionWidget bbbb_lsosl_widget;
	
	private void setbbbb_lsosl_widget(BooleanOptionWidget widget) {
		bbbb_lsosl_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lsosl_widget() {
		return bbbb_lsosl_widget;
	}	
	
	private BooleanOptionWidget bbbb_lsosl2_widget;
	
	private void setbbbb_lsosl2_widget(BooleanOptionWidget widget) {
		bbbb_lsosl2_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lsosl2_widget() {
		return bbbb_lsosl2_widget;
	}	
	
	private BooleanOptionWidget bbbb_lsosll_widget;
	
	private void setbbbb_lsosll_widget(BooleanOptionWidget widget) {
		bbbb_lsosll_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lsosll_widget() {
		return bbbb_lsosll_widget;
	}	
	
	private BooleanOptionWidget bbbb_lsosll2_widget;
	
	private void setbbbb_lsosll2_widget(BooleanOptionWidget widget) {
		bbbb_lsosll2_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lsosll2_widget() {
		return bbbb_lsosll2_widget;
	}	
	
	private BooleanOptionWidget bbbb_phodisabled_widget;
	
	private void setbbbb_phodisabled_widget(BooleanOptionWidget widget) {
		bbbb_phodisabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_phodisabled_widget() {
		return bbbb_phodisabled_widget;
	}	
	
	private BooleanOptionWidget bbbb_uledisabled_widget;
	
	private void setbbbb_uledisabled_widget(BooleanOptionWidget widget) {
		bbbb_uledisabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_uledisabled_widget() {
		return bbbb_uledisabled_widget;
	}	
	
	private BooleanOptionWidget bbbb_lpdisabled_widget;
	
	private void setbbbb_lpdisabled_widget(BooleanOptionWidget widget) {
		bbbb_lpdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lpdisabled_widget() {
		return bbbb_lpdisabled_widget;
	}	
	
	private BooleanOptionWidget bbbb_lpunsplit_original_locals_widget;
	
	private void setbbbb_lpunsplit_original_locals_widget(BooleanOptionWidget widget) {
		bbbb_lpunsplit_original_locals_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lpunsplit_original_locals_widget() {
		return bbbb_lpunsplit_original_locals_widget;
	}	
	
	private BooleanOptionWidget bopdisabled_widget;
	
	private void setbopdisabled_widget(BooleanOptionWidget widget) {
		bopdisabled_widget = widget;
	}
	
	public BooleanOptionWidget getbopdisabled_widget() {
		return bopdisabled_widget;
	}	
	
	private BooleanOptionWidget tagdisabled_widget;
	
	private void settagdisabled_widget(BooleanOptionWidget widget) {
		tagdisabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagdisabled_widget() {
		return tagdisabled_widget;
	}	
	
	private BooleanOptionWidget tagtag_lndisabled_widget;
	
	private void settagtag_lndisabled_widget(BooleanOptionWidget widget) {
		tagtag_lndisabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagtag_lndisabled_widget() {
		return tagtag_lndisabled_widget;
	}	
	
	private BooleanOptionWidget tagtag_andisabled_widget;
	
	private void settagtag_andisabled_widget(BooleanOptionWidget widget) {
		tagtag_andisabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagtag_andisabled_widget() {
		return tagtag_andisabled_widget;
	}	
	
	private BooleanOptionWidget tagtag_depdisabled_widget;
	
	private void settagtag_depdisabled_widget(BooleanOptionWidget widget) {
		tagtag_depdisabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagtag_depdisabled_widget() {
		return tagtag_depdisabled_widget;
	}	
	
	private BooleanOptionWidget tagtag_fieldrwdisabled_widget;
	
	private void settagtag_fieldrwdisabled_widget(BooleanOptionWidget widget) {
		tagtag_fieldrwdisabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagtag_fieldrwdisabled_widget() {
		return tagtag_fieldrwdisabled_widget;
	}	
	

	private ListOptionWidget Single_File_Mode_Optionsprocess_path_widget;
	
	private void setSingle_File_Mode_Optionsprocess_path_widget(ListOptionWidget widget) {
		Single_File_Mode_Optionsprocess_path_widget = widget;
	}
	
	public ListOptionWidget getSingle_File_Mode_Optionsprocess_path_widget() {
		return Single_File_Mode_Optionsprocess_path_widget;
	}	
	
	
	private BooleanOptionWidget Application_Mode_Optionsanalyze_context_widget;
	
	private void setApplication_Mode_Optionsanalyze_context_widget(BooleanOptionWidget widget) {
		Application_Mode_Optionsanalyze_context_widget = widget;
	}
	
	public BooleanOptionWidget getApplication_Mode_Optionsanalyze_context_widget() {
		return Application_Mode_Optionsanalyze_context_widget;
	}	
	

	private ListOptionWidget Application_Mode_Optionsinclude_widget;
	
	private void setApplication_Mode_Optionsinclude_widget(ListOptionWidget widget) {
		Application_Mode_Optionsinclude_widget = widget;
	}
	
	public ListOptionWidget getApplication_Mode_Optionsinclude_widget() {
		return Application_Mode_Optionsinclude_widget;
	}	
	
	

	private ListOptionWidget Application_Mode_Optionsexclude_widget;
	
	private void setApplication_Mode_Optionsexclude_widget(ListOptionWidget widget) {
		Application_Mode_Optionsexclude_widget = widget;
	}
	
	public ListOptionWidget getApplication_Mode_Optionsexclude_widget() {
		return Application_Mode_Optionsexclude_widget;
	}	
	
	

	private ListOptionWidget Application_Mode_Optionsdynamic_classes_widget;
	
	private void setApplication_Mode_Optionsdynamic_classes_widget(ListOptionWidget widget) {
		Application_Mode_Optionsdynamic_classes_widget = widget;
	}
	
	public ListOptionWidget getApplication_Mode_Optionsdynamic_classes_widget() {
		return Application_Mode_Optionsdynamic_classes_widget;
	}	
	
	

	private ListOptionWidget Application_Mode_Optionsdynamic_path_widget;
	
	private void setApplication_Mode_Optionsdynamic_path_widget(ListOptionWidget widget) {
		Application_Mode_Optionsdynamic_path_widget = widget;
	}
	
	public ListOptionWidget getApplication_Mode_Optionsdynamic_path_widget() {
		return Application_Mode_Optionsdynamic_path_widget;
	}	
	
	

	private ListOptionWidget Application_Mode_Optionsdynamic_package_widget;
	
	private void setApplication_Mode_Optionsdynamic_package_widget(ListOptionWidget widget) {
		Application_Mode_Optionsdynamic_package_widget = widget;
	}
	
	public ListOptionWidget getApplication_Mode_Optionsdynamic_package_widget() {
		return Application_Mode_Optionsdynamic_package_widget;
	}	
	
	
	private BooleanOptionWidget Input_Attribute_Optionskeep_line_number_widget;
	
	private void setInput_Attribute_Optionskeep_line_number_widget(BooleanOptionWidget widget) {
		Input_Attribute_Optionskeep_line_number_widget = widget;
	}
	
	public BooleanOptionWidget getInput_Attribute_Optionskeep_line_number_widget() {
		return Input_Attribute_Optionskeep_line_number_widget;
	}	
	
	private BooleanOptionWidget Input_Attribute_Optionskeep_offset_widget;
	
	private void setInput_Attribute_Optionskeep_offset_widget(BooleanOptionWidget widget) {
		Input_Attribute_Optionskeep_offset_widget = widget;
	}
	
	public BooleanOptionWidget getInput_Attribute_Optionskeep_offset_widget() {
		return Input_Attribute_Optionskeep_offset_widget;
	}	
	
	private BooleanOptionWidget Annotation_Optionsannot_nullpointer_widget;
	
	private void setAnnotation_Optionsannot_nullpointer_widget(BooleanOptionWidget widget) {
		Annotation_Optionsannot_nullpointer_widget = widget;
	}
	
	public BooleanOptionWidget getAnnotation_Optionsannot_nullpointer_widget() {
		return Annotation_Optionsannot_nullpointer_widget;
	}	
	
	private BooleanOptionWidget Annotation_Optionsannot_arraybounds_widget;
	
	private void setAnnotation_Optionsannot_arraybounds_widget(BooleanOptionWidget widget) {
		Annotation_Optionsannot_arraybounds_widget = widget;
	}
	
	public BooleanOptionWidget getAnnotation_Optionsannot_arraybounds_widget() {
		return Annotation_Optionsannot_arraybounds_widget;
	}	
	
	private BooleanOptionWidget Annotation_Optionsannot_side_effect_widget;
	
	private void setAnnotation_Optionsannot_side_effect_widget(BooleanOptionWidget widget) {
		Annotation_Optionsannot_side_effect_widget = widget;
	}
	
	public BooleanOptionWidget getAnnotation_Optionsannot_side_effect_widget() {
		return Annotation_Optionsannot_side_effect_widget;
	}	
	
	private BooleanOptionWidget Annotation_Optionsannot_fieldrw_widget;
	
	private void setAnnotation_Optionsannot_fieldrw_widget(BooleanOptionWidget widget) {
		Annotation_Optionsannot_fieldrw_widget = widget;
	}
	
	public BooleanOptionWidget getAnnotation_Optionsannot_fieldrw_widget() {
		return Annotation_Optionsannot_fieldrw_widget;
	}	
	
	private BooleanOptionWidget Miscellaneous_Optionstime_widget;
	
	private void setMiscellaneous_Optionstime_widget(BooleanOptionWidget widget) {
		Miscellaneous_Optionstime_widget = widget;
	}
	
	public BooleanOptionWidget getMiscellaneous_Optionstime_widget() {
		return Miscellaneous_Optionstime_widget;
	}	
	
	private BooleanOptionWidget Miscellaneous_Optionssubtract_gc_widget;
	
	private void setMiscellaneous_Optionssubtract_gc_widget(BooleanOptionWidget widget) {
		Miscellaneous_Optionssubtract_gc_widget = widget;
	}
	
	public BooleanOptionWidget getMiscellaneous_Optionssubtract_gc_widget() {
		return Miscellaneous_Optionssubtract_gc_widget;
	}	
	

	private Composite General_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("General Options");
	 	OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"h";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionshelp_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Help", "", "","h", "display help and exit", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"version";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionsversion_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Version", "", "","version", "output version information and exit", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"v";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionsverbose_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Verbose", "", "","v", "verbose mode", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"app";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionsapp_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Application Mode", "", "","app", "runs in application mode", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"w";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionswhole_program_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Whole-Program Mode", "", "","w", "runs in whole-program mode", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite Input_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Input Options");
	 	OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"allow-phantom-refs";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setInput_Optionsallow_phantom_refs_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Allow Phantom References", "", "","allow-phantom-refs", "allow unresolved classes; may cause errors", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Class File",
		"c",
		"Use class for source of Soot",
		
		true),
		
		new OptionData("Jimple File",
		"J",
		"Use Jimple for source of Soot",
		
		false),
		
		};
		
										
		setInput_Optionssrc_prec_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Input Source Precedence", "", "","src-prec", "sets the source precedence for Soot")));
		
		defKey = ""+" "+""+" "+"src-prec";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getInput_Optionssrc_prec_widget().setDef(defaultString);
		}
		
		
		
		defKey = ""+" "+""+" "+"cp";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setInput_Optionssoot_classpath_widget(new StringOptionWidget(editGroup, SWT.NONE, new OptionData("Soot Classpath",  "", "","cp", "uses given PATH as the classpath for finding classes for Soot processing", defaultString)));
		

		
		return editGroup;
	}



	private Composite Output_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Output Options");
	 	OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"via-grimp";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setOutput_Optionsvia_grimp_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Via Grimp", "", "","via-grimp", "convert jimple to bytecode via grimp instead of via baf", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"xml-attributes";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setOutput_Optionsxml_attributes_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Save Tags to XML", "", "","xml-attributes", "Save tags to XML attributes for Eclipse", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Jimp File",
		"j",
		"produce .jimp (abbreviated .jimple) files",
		
		false),
		
		new OptionData("Jimple File",
		"J",
		"produce .jimple code",
		
		false),
		
		new OptionData("Baf File",
		"B",
		"produce .baf code",
		
		false),
		
		new OptionData("Aggregated Baf File",
		"b",
		"produce .b (abbreviated .baf) files",
		
		false),
		
		new OptionData("Grimp File",
		"g",
		"produce .grimp (abbreviated .grimple) files",
		
		false),
		
		new OptionData("Grimple File",
		"G",
		"produce .grimple files",
		
		false),
		
		new OptionData("Xml File",
		"X",
		"produce .xml files",
		
		false),
		
		new OptionData("No Output File",
		"n",
		"produces no output",
		
		false),
		
		new OptionData("Jasmin File",
		"s",
		"produce .jasmin files",
		
		false),
		
		new OptionData("Class File",
		"c",
		"produce .class files",
		
		true),
		
		new OptionData("Dava Decompiled File",
		"d",
		"produce dava decompiled .java files",
		
		false),
		
		};
		
										
		setOutput_Optionsoutput_format_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Output Format", "", "","o", "sets the source precedence for Soot")));
		
		defKey = ""+" "+""+" "+"o";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getOutput_Optionsoutput_format_widget().setDef(defaultString);
		}
		
		
		
		defKey = ""+" "+""+" "+"d";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setOutput_Optionsoutput_dir_widget(new StringOptionWidget(editGroup, SWT.NONE, new OptionData("Output Directory",  "", "","d", "store produced files in PATH", defaultString)));
		

		
		return editGroup;
	}



	private Composite Processing_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Processing Options");
	 	OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"O";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setProcessing_Optionsoptimize_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Optimize", "", "","O", "perform scalar optimizations on the classfiles", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"W";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setProcessing_Optionswhole_optimize_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Whole Program Optimize", "", "","W", "perform whole program optimizations on the classfiles", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"via-shimple";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setProcessing_Optionsvia_shimple_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Via Shimple", "", "","via-shimple", "enables phases operating on Shimple SSA representation", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Jimple Body Creation");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"no-splitting";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbno_splitting_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("No Splitting", "p", "jb","no-splitting", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"no-typing";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbno_typing_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("No Typing", "p", "jb","no-typing", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"aggregate-all-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbaggregate_all_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Aggregate All Locals", "p", "jb","aggregate-all-locals", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"no-aggregating";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbno_aggregating_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("No Aggregating", "p", "jb","no-aggregating", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"use-original-names";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbuse_original_names_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Use Original Names", "p", "jb","use-original-names", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"pack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbpack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Pack Locals", "p", "jb","pack-locals", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"no-cp";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbno_cp_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("No Copy Propogator", "p", "jb","no-cp", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"no-nop-elimination";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbno_nop_elimination_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("No Nop Elimination", "p", "jb","no-nop-elimination", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"no-unreachable-code-elimination";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbno_unreachable_code_elimination_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("No Unreachable Code Elimination", "p", "jb","no-unreachable-code-elimination", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"verbatim";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbverbatim_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Verbatim", "p", "jb","verbatim", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_asvCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Stack Variable Aggregation");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.asv"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_asvdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.asv","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.asv"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_asvonly_stack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Stack Locals", "p", "jb.asv","only-stack-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_ulpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unsplit-originals Local Packer");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.ulp"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_ulpdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.ulp","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.ulp"+" "+"unsplit-original-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_ulpunsplit_original_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Unsplit Original Locals", "p", "jb.ulp","unsplit-original-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_lnsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Local Name Standardizer");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.lns"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_lnsdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.lns","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.lns"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_lnsonly_stack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Stack Locals", "p", "jb.lns","only-stack-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_cpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Copy Propagator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.cp"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_cpdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.cp","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.cp"+" "+"only-regular-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_cponly_regular_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Regular Locals", "p", "jb.cp","only-regular-locals", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.cp"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_cponly_stack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Stack Locals", "p", "jb.cp","only-stack-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_daeCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Dead Assignment Eliminator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.dae"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_daedisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.dae","disabled", "\nThis phase eliminates assignment statements (to locals) with no uses.\n                        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.dae"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_daeonly_stack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Stack Locals", "p", "jb.dae","only-stack-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_lsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Local Splitter");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.ls"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_lsdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.ls","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_aCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Aggregator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.a"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_adisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.a","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.a"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_aonly_stack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Stack Locals", "p", "jb.a","only-stack-locals", "Aggregate values stored in stack locals only.", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unused Local Eliminator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.ule"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_uledisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.ule","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_trCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Type Assigner");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.tr"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_trdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.tr","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_cp_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unused Local Eliminator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.cp-ule"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_cp_uledisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.cp-ule","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_lpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Local Packer");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.lp"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_lpdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.lp","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.lp"+" "+"unsplit-original-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_lpunsplit_original_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Unsplit Original Locals", "p", "jb.lp","unsplit-original-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_neCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Nop Eliminator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.ne"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_nedisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.ne","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jbjb_uceCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unreachable Code Eliminator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.uce"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_ucedisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jb.uce","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite cgCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Call Graph");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"cg"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "cg","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite cgcg_oldchaCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Old Class Hierarchy Analysis");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.oldcha"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_oldchadisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "cg.oldcha","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite cgcg_vtaCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Variable Type Analysis");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.vta"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_vtadisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "cg.vta","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.vta"+" "+"passes";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "1";
			
		}

		setcgcg_vtapasses_widget(new StringOptionWidget(editGroup, SWT.NONE, new OptionData("Number of passes",  "p", "cg.vta","passes", "", defaultString)));
		

		
		return editGroup;
	}



	private Composite cgcg_chaCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Spark Class Hierarchy Analysis");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.cha"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_chadisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "cg.cha","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.cha"+" "+"verbose";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_chaverbose_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Verbose", "p", "cg.cha","verbose", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite cgcg_sparkCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Spark");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparkdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "cg.spark","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite cgSpark_General_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Spark General Options");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"verbose";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkverbose_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Verbose", "p", "cg.spark","verbose", "\nWhen this option is set to true, Spark prints detailed information.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"ignore-types";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkignore_types_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Ignore Types Entirely", "p", "cg.spark","ignore-types", "\nWhen this option is set to true, all parts of Spark completely ignore\ndeclared types of variables and casts.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"force-gc";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkforce_gc_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Force Garbages Collections", "p", "cg.spark","force-gc", "\nWhen this option is set to true, calls to System.gc() will be made at\nvarious points to allow memory usage to be measured.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"pre-jimplify";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkpre_jimplify_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Pre Jimplify", "p", "cg.spark","pre-jimplify", "\n        When this option is set to true, convert all available methods to Jimple\nbefore starting the points-to analysis. This allows the Jimplification\ntime to be separated from the points-to time. However, it increases the\ntotal time and memory requirement, because all methods are Jimplified,\nrather than only those deemed reachable by the points-to analysis.\n        ", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite cgSpark_Pointer_Assignment_Graph_Building_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Spark Pointer Assignment Graph Building Options");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"vta";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkvta_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("VTA", "p", "cg.spark","vta", "\nSetting VTA to true has the effect of setting ignoreBaseObjects,\ntypesForSites, and simplifySCCs to true to simulate Variable Type\nAnalysis, described in sund.hend.ea00. Note that the\nalgorithm differs from the original VTA in that it handles array\nelements more precisely. To use the results of the analysis to trim the\ninvoke graph, set the trimInvokeGraph option to true as well.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"rta";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkrta_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("RTA", "p", "cg.spark","rta", "\nSetting RTA to true sets typesForSites to true, and causes Spark to use\na single points-to set for all variables, giving Rapid Type\nAnalysis baco.swee96.\nTo use the results of the analysis to trim the invoke graph, set the\ntrimInvokeGraph option to true as well.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"field-based";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkfield_based_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Field Based", "p", "cg.spark","field-based", "\nWhen this option is set to true, fields are represented by variable\n(Green) nodes, and the object that the field belongs to is ignored\n(all objects are lumped together), giving a field-based analysis. Otherwise, fields are represented by\nfield reference (Red) nodes, and the objects that they belong to are\ndistinguished, giving a field-sensitive analysis.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"types-for-sites";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparktypes_for_sites_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Types For Sites", "p", "cg.spark","types-for-sites", "\nWhen this option is set to true, types rather than allocation sites are\nused as the elements of the points-to sets.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"merge-stringbuffer";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparkmerge_stringbuffer_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Merge String Buffer", "p", "cg.spark","merge-stringbuffer", "\nWhen this option is set to true, all allocation sites creating\njava.lang.StringBuffer objects are grouped together as a single\nallocation site.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"simulate-natives";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparksimulate_natives_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Simulate Natives", "p", "cg.spark","simulate-natives", "\nWhen this option is set to true, effects of native methods are simulated.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"simple-edges-bidirectional";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparksimple_edges_bidirectional_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Simple Edges Bidirectional", "p", "cg.spark","simple-edges-bidirectional", "\nWhen this option is set to true, all edges connecting variable (Green)\nnodes are made bidirectional, as in Steensgaard's analysis stee96*1.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"on-fly-cg";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkon_fly_cg_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("On Fly Call Graph", "p", "cg.spark","on-fly-cg", "\nWhen this option is set to true, the call graph is computed on-the-fly\nas points-to information is computed. Otherwise, an initial\napproximation to the call graph is used.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"parms-as-fields";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkparms_as_fields_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Parms As Fields", "p", "cg.spark","parms-as-fields", "\nWhen this option is set to true, parameters to methods are represented\nas fields (Red nodes) of the this object; otherwise, parameters are\nrepresented as variable (Green) nodes.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"returns-as-fields";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkreturns_as_fields_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Returns As Fields", "p", "cg.spark","returns-as-fields", "\nWhen this option is set to true, return values from methods are\nrepresented as fields (Red nodes) of the this object; otherwise,\nreturn values are represented as variable (Green) nodes.\n        ", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite cgSpark_Pointer_Assignment_Graph_Simplification_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Spark Pointer Assignment Graph Simplification Options");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"simplify-offline";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparksimplify_offline_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Simplify Offline", "p", "cg.spark","simplify-offline", "\nWhen this option is set to true, variable (Green) nodes which are\nconnected by simple paths (so they must have the same points-to set) are\nmerged together.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"simplify-sccs";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparksimplify_sccs_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Simplify SCCs", "p", "cg.spark","simplify-sccs", "\nWhen this option is set to true, variable (Green) nodes which form\nstrongly-connected components (so they must have the same points-to set)\nare merged together.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"ignore-types-for-sccs";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkignore_types_for_sccs_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Ignore Types For SCCs", "p", "cg.spark","ignore-types-for-sccs", "\nWhen this option is set to true, when collapsing strongly-connected\ncomponents, nodes forming SCCs are collapsed regardless of their type.\nThe collapsed SCC is given the most general type of all the nodes in the\ncomponent.\n\nWhen this option is set to false, only edges connecting nodes of the\nsame type are considered when detecting SCCs.\n\nThis option has no effect unless simplifySCCs is true.\n        ", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite cgSpark_Points_To_Set_Flowing_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Spark Points-To Set Flowing Options");
	 	OptionData [] data;	
		

		
		data = new OptionData [] {
		
		new OptionData("Iter",
		"iter",
		"",
		
		false),
		
		new OptionData("Worklist",
		"worklist",
		"",
		
		true),
		
		new OptionData("Merge",
		"merge",
		"",
		
		false),
		
		new OptionData("Alias",
		"alias",
		"",
		
		false),
		
		new OptionData("None",
		"none",
		"",
		
		false),
		
		};
		
										
		setcgcg_sparkpropagator_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Propagator", "p", "cg.spark","propagator", "\nThis option tells Spark which propagation algorithm to use.\n\nIter is a simple, iterative algorithm, which propagates everything until the\ngraph does not change.\n\nWorklist is a worklist-based algorithm that tries\nto do as little work as possible. This is currently the fastest algorithm.\n\nAlias is an alias-edge based algorithm. This algorithm tends to take\nthe least memory for very large problems, because it does not represent\nexplicitly points-to sets of fields of heap objects.\n\nMerge is an algorithm that merges all yellow nodes with their corresponding\nred nodes. This algorithm is not yet finished.\n\nNone means that propagation is not done; the graph is only built and\nsimplified.\n        ")));
		
		defKey = "p"+" "+"cg.spark"+" "+"propagator";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getcgcg_sparkpropagator_widget().setDef(defaultString);
		}
		
		
		
		data = new OptionData [] {
		
		new OptionData("Hash",
		"hash",
		"",
		
		false),
		
		new OptionData("Bit",
		"bit",
		"",
		
		false),
		
		new OptionData("Hybrid",
		"hybrid",
		"",
		
		false),
		
		new OptionData("Array",
		"array",
		"",
		
		false),
		
		new OptionData("Double",
		"double",
		"",
		
		true),
		
		};
		
										
		setcgcg_sparkset_impl_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Set Implementation", "p", "cg.spark","set-impl", "\nSelects an implementation of a points-to set that Spark should use.\n\nHash is an implementation based on Java's built-in hash-set.\n\nBit is an implementation using a bit vector.\n\nHybrid is an implementation that keeps an explicit list of up to\n16 elements, and switches to using a bit-vector when the set gets\nlarger than this.\n\nArray is an implementation that keeps the elements of the points-to set\nin an array that is always maintained in sorted order. Set membership is\ntested using binary search, and set union and intersection are computed\nusing an algorithm based on the merge step from merge sort.\n\nDouble is an implementation that itself uses a pair of sets for\neach points-to set. The first set in the pair stores new pointed-to\nobjects that have not yet been propagated, while the second set stores\nold pointed-to objects that have been propagated and need not be\nreconsidered. This allows the propagation algorithms to be incremental,\noften speeding them up significantly.\n        ")));
		
		defKey = "p"+" "+"cg.spark"+" "+"set-impl";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getcgcg_sparkset_impl_widget().setDef(defaultString);
		}
		
		
		
		data = new OptionData [] {
		
		new OptionData("Hash",
		"hash",
		"",
		
		false),
		
		new OptionData("Bit",
		"bit",
		"",
		
		false),
		
		new OptionData("Hybrid",
		"hybrid",
		"",
		
		true),
		
		new OptionData("Array",
		"array",
		"",
		
		false),
		
		};
		
										
		setcgcg_sparkdouble_set_old_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Double Set Old", "p", "cg.spark","double-set-old", "\nSelects an implementation for the sets of old objects in the double\npoints-to set implementation.\n\nThis option has no effect unless setImpl is set to double.\n        ")));
		
		defKey = "p"+" "+"cg.spark"+" "+"double-set-old";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getcgcg_sparkdouble_set_old_widget().setDef(defaultString);
		}
		
		
		
		data = new OptionData [] {
		
		new OptionData("Hash",
		"hash",
		"",
		
		false),
		
		new OptionData("Bit",
		"bit",
		"",
		
		false),
		
		new OptionData("Hybrid",
		"hybrid",
		"",
		
		true),
		
		new OptionData("Array",
		"array",
		"",
		
		false),
		
		};
		
										
		setcgcg_sparkdouble_set_new_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Double Set New", "p", "cg.spark","double-set-new", "\nSelects an implementation for the sets of new objects in the double\npoints-to set implementation.\n\nThis option has no effect unless setImpl is set to double.\n        ")));
		
		defKey = "p"+" "+"cg.spark"+" "+"double-set-new";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getcgcg_sparkdouble_set_new_widget().setDef(defaultString);
		}
		
		

		
		return editGroup;
	}



	private Composite cgSpark_Output_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Spark Output Options");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"dump-html";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkdump_html_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Dump HTML", "p", "cg.spark","dump-html", "\nWhen this option is set to true, a browseable HTML representation of the\npointer assignment graph is output after the analysis completes. Note\nthat this representation is typically very large.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"dump-pag";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkdump_pag_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Dump PAG", "p", "cg.spark","dump-pag", "\nWhen this option is set to true, a representation of the pointer assignment graph\nsuitable for processing with other solvers (such as the BDD-based solver) is\noutput before the analysis begins.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"dump-solution";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkdump_solution_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Dump Solution", "p", "cg.spark","dump-solution", "\nWhen this option is set to true, a representation of the resulting points-to\nsets is dumped. The format is similar to that of the dumpPAG\noption, and is therefore suitable for comparison with the results of other\nsolvers.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"topo-sort";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparktopo_sort_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Topological Sort", "p", "cg.spark","topo-sort", "\nWhen this option is set to true, the representation dumped by the dumpPAG option\nis dumped with the green nodes in (pseudo-)topological order.\n\nThis option has no effect unless dumpPAG is true.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"dump-types";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparkdump_types_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Dump Types", "p", "cg.spark","dump-types", "\nWhen this option is set to true, the representation dumped by the dumpPAG option\nincludes type information for all nodes.\n\nThis option has no effect unless dumpPAG is true.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"class-method-var";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparkclass_method_var_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Class Method Var", "p", "cg.spark","class-method-var", "\nWhen this option is set to true, the representation dumped by the dumpPAG option\nrepresents nodes by numbering each class, method, and variable within\nthe method separately, rather than assigning a single integer to each\nnode.\n\nThis option has no effect unless dumpPAG is true.\nSetting classMethodVar to true has the effect of setting topoSort to false.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"dump-answer";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkdump_answer_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Dump Answer", "p", "cg.spark","dump-answer", "\nWhen this option is set to true, the computed reaching types for each variable are\ndumped to a file, so that they can be compared with the results of\nother analyses (such as the old VTA).\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"trim-invoke-graph";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparktrim_invoke_graph_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Trim Invoke Graph", "p", "cg.spark","trim-invoke-graph", "\nWhen this option is set to true, the results of the analysis are used to make the invoke graph\nmore precise after the analysis completes.\n        ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"add-tags";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkadd_tags_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Add Tags", "p", "cg.spark","add-tags", "\n        When this option is set to true, the results of the analysis are encoded inside\ntags, and printed with the resulting Jimple code.\n\n        ", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite wstpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Whole Shimple Transformation Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"wstp"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setwstpdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "wstp","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite wsopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Whole Shimple Optimization Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"wsop"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwsopdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "wsop","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite wjtpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Whole-Jimple Transformation Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"wjtp"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setwjtpdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "wjtp","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite wjopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Whole-Jimple Optimization Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"wjop"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "wjop","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite wjopwjop_smbCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Static Method Binding");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"wjop.smb"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_smbdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "wjop.smb","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"wjop.smb"+" "+"insert-null-checks";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_smbinsert_null_checks_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Insert Null Checks", "p", "wjop.smb","insert-null-checks", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"wjop.smb"+" "+"insert-redundant-casts";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_smbinsert_redundant_casts_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Insert Redundant Casts", "p", "wjop.smb","insert-redundant-casts", "", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Unsafe",
		"unsafe",
		"",
		
		true),
		
		new OptionData("Safe",
		"safe",
		"",
		
		false),
		
		new OptionData("None",
		"none",
		"",
		
		false),
		
		};
		
										
		setwjopwjop_smballowed_modifier_changes_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Allow Modifier Changes", "p", "wjop.smb","allowed-modifier-changes", "")));
		
		defKey = "p"+" "+"wjop.smb"+" "+"allowed-modifier-changes";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getwjopwjop_smballowed_modifier_changes_widget().setDef(defaultString);
		}
		
		

		
		return editGroup;
	}



	private Composite wjopwjop_siCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Static Inlining");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"wjop.si"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setwjopwjop_sidisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "wjop.si","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"wjop.si"+" "+"insert-null-checks";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_siinsert_null_checks_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Insert Null Checks", "p", "wjop.si","insert-null-checks", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"wjop.si"+" "+"insert-redundant-casts";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_siinsert_redundant_casts_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Insert Redundant Casts", "p", "wjop.si","insert-redundant-casts", "", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Unsafe",
		"unsafe",
		"",
		
		true),
		
		new OptionData("Safe",
		"safe",
		"",
		
		false),
		
		new OptionData("None",
		"none",
		"",
		
		false),
		
		};
		
										
		setwjopwjop_siallowed_modifier_changes_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Allow Modifier Changes", "p", "wjop.si","allowed-modifier-changes", "")));
		
		defKey = "p"+" "+"wjop.si"+" "+"allowed-modifier-changes";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getwjopwjop_siallowed_modifier_changes_widget().setDef(defaultString);
		}
		
		
		
		defKey = "p"+" "+"wjop.si"+" "+"expansion-factor";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "3";
			
		}

		setwjopwjop_siexpansion_factor_widget(new StringOptionWidget(editGroup, SWT.NONE, new OptionData("Expansion Factor",  "p", "wjop.si","expansion-factor", "", defaultString)));
		
		
		defKey = "p"+" "+"wjop.si"+" "+"max-container-size";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "5000";
			
		}

		setwjopwjop_simax_container_size_widget(new StringOptionWidget(editGroup, SWT.NONE, new OptionData("Max Container Size",  "p", "wjop.si","max-container-size", "", defaultString)));
		
		
		defKey = "p"+" "+"wjop.si"+" "+"max-inlinee-size";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "20";
			
		}

		setwjopwjop_simax_inlinee_size_widget(new StringOptionWidget(editGroup, SWT.NONE, new OptionData("Max Inline Size",  "p", "wjop.si","max-inlinee-size", "", defaultString)));
		

		
		return editGroup;
	}



	private Composite wjapCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Whole Jimple Annotation Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"wjap"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setwjapdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "wjap","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite wjapwjap_raCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Rectangular Array Finder");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"wjap.ra"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjapwjap_radisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "wjap.ra","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite stpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Shimple Transformation Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"stp"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setstpdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "stp","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite sopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Shimple Optimization Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"sop"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setsopdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "sop","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jtpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Jimple Transformations Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jtp"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjtpdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jtp","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Jimple Optimizations Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_cseCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Common Subexpression Elimination");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.cse"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_csedisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.cse","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.cse"+" "+"naive-side-effect";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_csenaive_side_effect_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Naive Side Effect Tester", "p", "jop.cse","naive-side-effect", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_bcmCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Busy Code Motion");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.bcm"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_bcmdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.bcm","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.bcm"+" "+"naive-side-effect";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_bcmnaive_side_effect_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Naive Side Effect Tester", "p", "jop.bcm","naive-side-effect", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_lcmCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Lazy Code Motion");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.lcm"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_lcmdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.lcm","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.lcm"+" "+"unroll";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_lcmunroll_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Unroll", "p", "jop.lcm","unroll", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.lcm"+" "+"naive-side-effect";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_lcmnaive_side_effect_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Naive Side Effect Tester", "p", "jop.lcm","naive-side-effect", "", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Safe",
		"safe",
		"",
		
		true),
		
		new OptionData("Medium",
		"medium",
		"",
		
		false),
		
		new OptionData("Unsafe",
		"unsafe",
		"",
		
		false),
		
		};
		
										
		setjopjop_lcmsafe_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Safe", "p", "jop.lcm","safe", "")));
		
		defKey = "p"+" "+"jop.lcm"+" "+"safe";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getjopjop_lcmsafe_widget().setDef(defaultString);
		}
		
		

		
		return editGroup;
	}



	private Composite jopjop_cpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Copy Propogator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.cp"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_cpdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.cp","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.cp"+" "+"only-regular-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_cponly_regular_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Regular Locals", "p", "jop.cp","only-regular-locals", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.cp"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_cponly_stack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Stack Locals", "p", "jop.cp","only-stack-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_cpfCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Constant Propagator and Folder");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.cpf"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_cpfdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.cpf","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_cbfCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Conditional Branch Folder");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.cbf"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_cbfdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.cbf","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_daeCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Dead Assignment Eliminator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.dae"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_daedisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.dae","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.dae"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_daeonly_stack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Stack Locals", "p", "jop.dae","only-stack-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_uce1Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unreachable Code Eliminator 1");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.uce1"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_uce1disabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.uce1","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_uce2Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unreachable Code Eliminator 2");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.uce2"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_uce2disabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.uce2","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_ubf1Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unconditional Branch Folder 1");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.ubf1"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_ubf1disabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.ubf1","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_ubf2Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unconditional Branch Folder 2");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.ubf2"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_ubf2disabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.ubf2","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite jopjop_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unused Local Eliminator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.ule"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_uledisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jop.ule","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite japCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Jimple Annotation Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jap"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jap","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite japjap_npcCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Null Pointer Check Options");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.npc"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjapjap_npcdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jap.npc","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.npc"+" "+"only-array-ref";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_npconly_array_ref_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Array Ref", "p", "jap.npc","only-array-ref", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.npc"+" "+"profiling";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_npcprofiling_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Profiling", "p", "jap.npc","profiling", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite japjap_abcCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Array Bound Check Options");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.abc"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjapjap_abcdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jap.abc","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-all";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_all_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("With All", "p", "jap.abc","with-all", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-fieldref";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_fieldref_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("With Field References", "p", "jap.abc","with-fieldref", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-arrayref";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_arrayref_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("With Array References", "p", "jap.abc","with-arrayref", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-cse";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_cse_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("With Common Sub-expressions", "p", "jap.abc","with-cse", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-classfield";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_classfield_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("With Class Field", "p", "jap.abc","with-classfield", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-rectarray";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_rectarray_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("With Rectangular Array", "p", "jap.abc","with-rectarray", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"profiling";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcprofiling_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Profiling", "p", "jap.abc","profiling", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite japjap_profilingCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Profiling Generator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.profiling"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjapjap_profilingdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jap.profiling","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.profiling"+" "+"notmainentry";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_profilingnotmainentry_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Not Main Entry", "p", "jap.profiling","notmainentry", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite japjap_seaCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Side effect tagger");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.sea"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjapjap_seadisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jap.sea","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.sea"+" "+"naive";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_seanaive_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Build naive dependence graph", "p", "jap.sea","naive", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite japjap_fieldrwCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Field Read/Write Tagger");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.fieldrw"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjapjap_fieldrwdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "jap.fieldrw","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.fieldrw"+" "+"threshold";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "100";
			
		}

		setjapjap_fieldrwthreshold_widget(new StringOptionWidget(editGroup, SWT.NONE, new OptionData("Maximum number of fields",  "p", "jap.fieldrw","threshold", "", defaultString)));
		

		
		return editGroup;
	}



	private Composite gbCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Grimp Body Creation");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"gb"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setgbdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "gb","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite gbgb_a1Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Aggregator 1");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"gb.a1"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setgbgb_a1disabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "gb.a1","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"gb.a1"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgbgb_a1only_stack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Stack Locals", "p", "gb.a1","only-stack-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite gbgb_cfCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Constructor Folder");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"gb.cf"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setgbgb_cfdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "gb.cf","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite gbgb_a2Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Aggregator 2");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"gb.a2"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setgbgb_a2disabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "gb.a2","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"gb.a2"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgbgb_a2only_stack_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Only Stack Locals", "p", "gb.a2","only-stack-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite gbgb_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unused Local Eliminator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"gb.ule"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setgbgb_uledisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "gb.ule","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite gopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Grimp Optimization Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"gop"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgopdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "gop","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite bbCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Baf Body Creation");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"bb"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "bb","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite bbbb_lsoCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Load Store Optimizer");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"bb.lso"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lsodisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "bb.lso","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"debug";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lsodebug_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Debug", "p", "bb.lso","debug", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"inter";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lsointer_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Inter", "p", "bb.lso","inter", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"sl";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbbbb_lsosl_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("sl", "p", "bb.lso","sl", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"sl2";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lsosl2_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("sl2", "p", "bb.lso","sl2", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"sll";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbbbb_lsosll_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("sll", "p", "bb.lso","sll", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"sll2";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lsosll2_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("sll2", "p", "bb.lso","sll2", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite bbbb_phoCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Peephole Optimizer");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"bb.pho"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_phodisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "bb.pho","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite bbbb_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Unused Local Eliminator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"bb.ule"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_uledisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "bb.ule","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite bbbb_lpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Local Packer");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"bb.lp"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lpdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "bb.lp","disabled", "", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lp"+" "+"unsplit-original-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lpunsplit_original_locals_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Unsplit Original Locals", "p", "bb.lp","unsplit-original-locals", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite bopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Baf Optimization Pack");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"bop"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbopdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "bop","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite tagCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Tag");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"tag"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		settagdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "tag","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite tagtag_lnCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Line Number Tag Aggregator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"tag.ln"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		settagtag_lndisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "tag.ln","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite tagtag_anCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Array Bounds and Null Pointer Check Tag Aggregator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"tag.an"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		settagtag_andisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "tag.an","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite tagtag_depCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Dependence Tag Aggregator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"tag.dep"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		settagtag_depdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "tag.dep","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite tagtag_fieldrwCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Field Read/Write Tag Aggregator");
	 	OptionData [] data;	
		

		
		defKey = "p"+" "+"tag.fieldrw"+" "+"disabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		settagtag_fieldrwdisabled_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Disabled", "p", "tag.fieldrw","disabled", "", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite Single_File_Mode_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Single File Mode Options");
	 	OptionData [] data;	
		


		defKey = ""+" "+""+" "+"process-path";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setSingle_File_Mode_Optionsprocess_path_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Process Path",  "", "","process-path", "process all classes on the PATH", defaultString)));
		

		
		return editGroup;
	}



	private Composite Application_Mode_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Application Mode Options");
	 	OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"a";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setApplication_Mode_Optionsanalyze_context_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Analyze Context", "", "","a", "label context classes as library", defaultBool)));
		
		

		defKey = ""+" "+""+" "+"i";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsinclude_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Include Package",  "", "","i", "marks classfiles in PACKAGE (e.g. java.util.)as application classes", defaultString)));
		

		defKey = ""+" "+""+" "+"x";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsexclude_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Exclude Package",  "", "","x", "marks classfiles in PACKAGE (e.g. java.) as context classes", defaultString)));
		

		defKey = ""+" "+""+" "+"dynamic-classes";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsdynamic_classes_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Dynamic Classes",  "", "","dynamic-classes", "marks CLASSES (separated by colons) as potentially dynamic classes", defaultString)));
		

		defKey = ""+" "+""+" "+"dynamic-path";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsdynamic_path_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Dynamic Path",  "", "","dynamic-path", "marks all class files in PATH as potentially dynamic classes", defaultString)));
		

		defKey = ""+" "+""+" "+"dynamic-package";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsdynamic_package_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Dynamic Package",  "", "","dynamic-package", "marks classfiles in PACKAGES (separated by commas) as potentially dynamic classes", defaultString)));
		

		
		return editGroup;
	}



	private Composite Input_Attribute_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Input Attribute Options");
	 	OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"keep-line-number";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setInput_Attribute_Optionskeep_line_number_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Keep Line Number", "", "","keep-line-number", "keep line number tables", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"keep-bytecode-offset";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setInput_Attribute_Optionskeep_offset_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Keep Bytecode Offset", "", "","keep-bytecode-offset", "attach bytecode offset to jimple statement", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite Annotation_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Annotation Options");
	 	OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"annot-nullpointer";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setAnnotation_Optionsannot_nullpointer_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Null Pointer Annotation", "", "","annot-nullpointer", "turn on the annotation for null pointer", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"annot-arraybounds";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setAnnotation_Optionsannot_arraybounds_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Array Bounds Annotation", "", "","annot-arraybounds", "turn on the annotation for array bounds check", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"annot-side-effect";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setAnnotation_Optionsannot_side_effect_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Side effect annotation", "", "","annot-side-effect", "turn on side-effect attributes", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"annot-fieldrw";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setAnnotation_Optionsannot_fieldrw_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Field read/write annotation", "", "","annot-fieldrw", "turn on field read/write attributes", defaultBool)));
		
		

		
		return editGroup;
	}



	private Composite Miscellaneous_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Miscellaneous Options");
	 	OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"time";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setMiscellaneous_Optionstime_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Time", "", "","time", "print out time statistics about tranformations", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"subtract-gc";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setMiscellaneous_Optionssubtract_gc_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Subtract Garbage Collection Time", "", "","subtract-gc", "attempt to subtract the gc from the time stats", defaultBool)));
		
		

		
		return editGroup;
	}




}

