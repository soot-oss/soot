

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

//import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
//import ca.mcgill.sable.soot.SootPlugin;
//import ca.mcgill.sable.soot.util.*;
import ca.mcgill.sable.soot.ui.*;
import java.util.*;
import org.eclipse.swt.events.*;

public class PhaseOptionsDialog extends AbstractOptionsDialog implements SelectionListener {

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

Composite wjtpChild = wjtpCreate(getPageContainer());

Composite wjopChild = wjopCreate(getPageContainer());

Composite wjapChild = wjapCreate(getPageContainer());

Composite shimpleChild = shimpleCreate(getPageContainer());

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

Composite jbjb_lsChild = jbjb_lsCreate(getPageContainer());

Composite jbjb_aChild = jbjb_aCreate(getPageContainer());

Composite jbjb_uleChild = jbjb_uleCreate(getPageContainer());

Composite jbjb_trChild = jbjb_trCreate(getPageContainer());

Composite jbjb_ulpChild = jbjb_ulpCreate(getPageContainer());

Composite jbjb_lnsChild = jbjb_lnsCreate(getPageContainer());

Composite jbjb_cpChild = jbjb_cpCreate(getPageContainer());

Composite jbjb_daeChild = jbjb_daeCreate(getPageContainer());

Composite jbjb_cp_uleChild = jbjb_cp_uleCreate(getPageContainer());

Composite jbjb_lpChild = jbjb_lpCreate(getPageContainer());

Composite jbjb_neChild = jbjb_neCreate(getPageContainer());

Composite jbjb_uceChild = jbjb_uceCreate(getPageContainer());

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

Composite sopsop_cpfChild = sopsop_cpfCreate(getPageContainer());

Composite jopjop_cseChild = jopjop_cseCreate(getPageContainer());

Composite jopjop_bcmChild = jopjop_bcmCreate(getPageContainer());

Composite jopjop_lcmChild = jopjop_lcmCreate(getPageContainer());

Composite jopjop_cpChild = jopjop_cpCreate(getPageContainer());

Composite jopjop_cpfChild = jopjop_cpfCreate(getPageContainer());

Composite jopjop_cbfChild = jopjop_cbfCreate(getPageContainer());

Composite jopjop_daeChild = jopjop_daeCreate(getPageContainer());

Composite jopjop_uce1Child = jopjop_uce1Create(getPageContainer());

Composite jopjop_ubf1Child = jopjop_ubf1Create(getPageContainer());

Composite jopjop_uce2Child = jopjop_uce2Create(getPageContainer());

Composite jopjop_ubf2Child = jopjop_ubf2Create(getPageContainer());

Composite jopjop_uleChild = jopjop_uleCreate(getPageContainer());

Composite japjap_npcChild = japjap_npcCreate(getPageContainer());

Composite japjap_abcChild = japjap_abcCreate(getPageContainer());

Composite japjap_profilingChild = japjap_profilingCreate(getPageContainer());

Composite japjap_seaChild = japjap_seaCreate(getPageContainer());

Composite japjap_fieldrwChild = japjap_fieldrwCreate(getPageContainer());

Composite japjap_cgtaggerChild = japjap_cgtaggerCreate(getPageContainer());

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


		initializeRadioGroups();
		initializeEnableGroups();
		
	}

	private void initializeRadioGroups(){
		setRadioGroups(new HashMap());
		int counter = 0;
		ArrayList buttonList;

		
		buttonList = new ArrayList();

			
		if (isEnableButton("enabled")) {
			buttonList.add(getcgcg_chaenabled_widget());	
			getcgcg_chaenabled_widget().getButton().addSelectionListener(this);
		}

			
		if (isEnableButton("verbose")) {
			buttonList.add(getcgcg_chaverbose_widget());	
			getcgcg_chaverbose_widget().getButton().addSelectionListener(this);
		}

			
		if (isEnableButton("enabled")) {
			buttonList.add(getcgcg_sparkenabled_widget());	
			getcgcg_sparkenabled_widget().getButton().addSelectionListener(this);
		}

		
		getRadioGroups().put(new Integer(counter), buttonList);

		counter++;
		
	}

	
	
	private void initializeEnableGroups(){
		setEnableGroups(new ArrayList());
		
		
		
		makeNewEnableGroup("jb");
		
		
		addToEnableGroup("jb", getjbenabled_widget(), "enabled");
		
		
		addToEnableGroup("jb", getjbuse_original_names_widget(), "use-original-names");
		
		
		getjbenabled_widget().getButton().addSelectionListener(this);
		
		getjbuse_original_names_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.ls");
		
		
		addToEnableGroup("jb", "jb.ls", getjbjb_lsenabled_widget(), "enabled");
		
		getjbjb_lsenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.a");
		
		
		addToEnableGroup("jb", "jb.a", getjbjb_aenabled_widget(), "enabled");
		
		addToEnableGroup("jb", "jb.a", getjbjb_aonly_stack_locals_widget(), "only-stack-locals");
		
		getjbjb_aenabled_widget().getButton().addSelectionListener(this);
		
		getjbjb_aonly_stack_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.ule");
		
		
		addToEnableGroup("jb", "jb.ule", getjbjb_uleenabled_widget(), "enabled");
		
		getjbjb_uleenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.tr");
		
		
		addToEnableGroup("jb", "jb.tr", getjbjb_trenabled_widget(), "enabled");
		
		getjbjb_trenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.ulp");
		
		
		addToEnableGroup("jb", "jb.ulp", getjbjb_ulpenabled_widget(), "enabled");
		
		addToEnableGroup("jb", "jb.ulp", getjbjb_ulpunsplit_original_locals_widget(), "unsplit-original-locals");
		
		getjbjb_ulpenabled_widget().getButton().addSelectionListener(this);
		
		getjbjb_ulpunsplit_original_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.lns");
		
		
		addToEnableGroup("jb", "jb.lns", getjbjb_lnsenabled_widget(), "enabled");
		
		addToEnableGroup("jb", "jb.lns", getjbjb_lnsonly_stack_locals_widget(), "only-stack-locals");
		
		getjbjb_lnsenabled_widget().getButton().addSelectionListener(this);
		
		getjbjb_lnsonly_stack_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.cp");
		
		
		addToEnableGroup("jb", "jb.cp", getjbjb_cpenabled_widget(), "enabled");
		
		addToEnableGroup("jb", "jb.cp", getjbjb_cponly_regular_locals_widget(), "only-regular-locals");
		
		addToEnableGroup("jb", "jb.cp", getjbjb_cponly_stack_locals_widget(), "only-stack-locals");
		
		getjbjb_cpenabled_widget().getButton().addSelectionListener(this);
		
		getjbjb_cponly_regular_locals_widget().getButton().addSelectionListener(this);
		
		getjbjb_cponly_stack_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.dae");
		
		
		addToEnableGroup("jb", "jb.dae", getjbjb_daeenabled_widget(), "enabled");
		
		addToEnableGroup("jb", "jb.dae", getjbjb_daeonly_stack_locals_widget(), "only-stack-locals");
		
		getjbjb_daeenabled_widget().getButton().addSelectionListener(this);
		
		getjbjb_daeonly_stack_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.cp-ule");
		
		
		addToEnableGroup("jb", "jb.cp-ule", getjbjb_cp_uleenabled_widget(), "enabled");
		
		getjbjb_cp_uleenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.lp");
		
		
		addToEnableGroup("jb", "jb.lp", getjbjb_lpenabled_widget(), "enabled");
		
		addToEnableGroup("jb", "jb.lp", getjbjb_lpunsplit_original_locals_widget(), "unsplit-original-locals");
		
		getjbjb_lpenabled_widget().getButton().addSelectionListener(this);
		
		getjbjb_lpunsplit_original_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.ne");
		
		
		addToEnableGroup("jb", "jb.ne", getjbjb_neenabled_widget(), "enabled");
		
		getjbjb_neenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jb", "jb.uce");
		
		
		addToEnableGroup("jb", "jb.uce", getjbjb_uceenabled_widget(), "enabled");
		
		getjbjb_uceenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("cg");
		
		
		addToEnableGroup("cg", getcgenabled_widget(), "enabled");
		
		
		addToEnableGroup("cg", getcgsafe_forname_widget(), "safe-forname");
		
		
		addToEnableGroup("cg", getcgverbose_widget(), "verbose");
		
		
		getcgenabled_widget().getButton().addSelectionListener(this);
		
		getcgsafe_forname_widget().getButton().addSelectionListener(this);
		
		getcgverbose_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("cg", "cg.cha");
		
		
		addToEnableGroup("cg", "cg.cha", getcgcg_chaenabled_widget(), "enabled");
		
		addToEnableGroup("cg", "cg.cha", getcgcg_chaverbose_widget(), "verbose");
		
		getcgcg_chaenabled_widget().getButton().addSelectionListener(this);
		
		getcgcg_chaverbose_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("cg", "cg.spark");
		
		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkenabled_widget(), "enabled");
		
		getcgcg_sparkenabled_widget().getButton().addSelectionListener(this);
		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkverbose_widget(), "verbose");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkignore_types_widget(), "ignore-types");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkforce_gc_widget(), "force-gc");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkpre_jimplify_widget(), "pre-jimplify");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkvta_widget(), "vta");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkrta_widget(), "rta");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkfield_based_widget(), "field-based");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparktypes_for_sites_widget(), "types-for-sites");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkmerge_stringbuffer_widget(), "merge-stringbuffer");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparksimulate_natives_widget(), "simulate-natives");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparksimple_edges_bidirectional_widget(), "simple-edges-bidirectional");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkon_fly_cg_widget(), "on-fly-cg");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkparms_as_fields_widget(), "parms-as-fields");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkreturns_as_fields_widget(), "returns-as-fields");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparksimplify_offline_widget(), "simplify-offline");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparksimplify_sccs_widget(), "simplify-sccs");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkignore_types_for_sccs_widget(), "ignore-types-for-sccs");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkpropagator_widget(), "propagator");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkset_impl_widget(), "set-impl");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkdouble_set_old_widget(), "double-set-old");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkdouble_set_new_widget(), "double-set-new");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkdump_html_widget(), "dump-html");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkdump_pag_widget(), "dump-pag");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkdump_solution_widget(), "dump-solution");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparktopo_sort_widget(), "topo-sort");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkdump_types_widget(), "dump-types");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkclass_method_var_widget(), "class-method-var");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkdump_answer_widget(), "dump-answer");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkadd_tags_widget(), "add-tags");

		
		addToEnableGroup("cg", "cg.spark", getcgcg_sparkset_mass_widget(), "set-mass");

		
		
		makeNewEnableGroup("wjtp");
		
		
		addToEnableGroup("wjtp", getwjtpenabled_widget(), "enabled");
		
		
		getwjtpenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("wjop");
		
		
		addToEnableGroup("wjop", getwjopenabled_widget(), "enabled");
		
		
		getwjopenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("wjop", "wjop.smb");
		
		
		addToEnableGroup("wjop", "wjop.smb", getwjopwjop_smbenabled_widget(), "enabled");
		
		addToEnableGroup("wjop", "wjop.smb", getwjopwjop_smbinsert_null_checks_widget(), "insert-null-checks");
		
		addToEnableGroup("wjop", "wjop.smb", getwjopwjop_smbinsert_redundant_casts_widget(), "insert-redundant-casts");
		
		addToEnableGroup("wjop", "wjop.smb", getwjopwjop_smballowed_modifier_changes_widget(), "allowed-modifier-changes");
		
		getwjopwjop_smbenabled_widget().getButton().addSelectionListener(this);
		
		getwjopwjop_smbinsert_null_checks_widget().getButton().addSelectionListener(this);
		
		getwjopwjop_smbinsert_redundant_casts_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("wjop", "wjop.si");
		
		
		addToEnableGroup("wjop", "wjop.si", getwjopwjop_sienabled_widget(), "enabled");
		
		addToEnableGroup("wjop", "wjop.si", getwjopwjop_siinsert_null_checks_widget(), "insert-null-checks");
		
		addToEnableGroup("wjop", "wjop.si", getwjopwjop_siinsert_redundant_casts_widget(), "insert-redundant-casts");
		
		addToEnableGroup("wjop", "wjop.si", getwjopwjop_siallowed_modifier_changes_widget(), "allowed-modifier-changes");
		
		addToEnableGroup("wjop", "wjop.si", getwjopwjop_siexpansion_factor_widget(), "expansion-factor");
		
		addToEnableGroup("wjop", "wjop.si", getwjopwjop_simax_container_size_widget(), "max-container-size");
		
		addToEnableGroup("wjop", "wjop.si", getwjopwjop_simax_inlinee_size_widget(), "max-inlinee-size");
		
		getwjopwjop_sienabled_widget().getButton().addSelectionListener(this);
		
		getwjopwjop_siinsert_null_checks_widget().getButton().addSelectionListener(this);
		
		getwjopwjop_siinsert_redundant_casts_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("wjap");
		
		
		addToEnableGroup("wjap", getwjapenabled_widget(), "enabled");
		
		
		getwjapenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("wjap", "wjap.ra");
		
		
		addToEnableGroup("wjap", "wjap.ra", getwjapwjap_raenabled_widget(), "enabled");
		
		getwjapwjap_raenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("shimple");
		
		
		addToEnableGroup("shimple", getshimpleenabled_widget(), "enabled");
		
		
		addToEnableGroup("shimple", getshimplephi_elim_opt_widget(), "phi-elim-opt");
		
		
		getshimpleenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("stp");
		
		
		addToEnableGroup("stp", getstpenabled_widget(), "enabled");
		
		
		getstpenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("sop");
		
		
		addToEnableGroup("sop", getsopenabled_widget(), "enabled");
		
		
		getsopenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("sop", "sop.cpf");
		
		
		addToEnableGroup("sop", "sop.cpf", getsopsop_cpfenabled_widget(), "enabled");
		
		getsopsop_cpfenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jtp");
		
		
		addToEnableGroup("jtp", getjtpenabled_widget(), "enabled");
		
		
		getjtpenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop");
		
		
		addToEnableGroup("jop", getjopenabled_widget(), "enabled");
		
		
		getjopenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.cse");
		
		
		addToEnableGroup("jop", "jop.cse", getjopjop_cseenabled_widget(), "enabled");
		
		addToEnableGroup("jop", "jop.cse", getjopjop_csenaive_side_effect_widget(), "naive-side-effect");
		
		getjopjop_cseenabled_widget().getButton().addSelectionListener(this);
		
		getjopjop_csenaive_side_effect_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.bcm");
		
		
		addToEnableGroup("jop", "jop.bcm", getjopjop_bcmenabled_widget(), "enabled");
		
		addToEnableGroup("jop", "jop.bcm", getjopjop_bcmnaive_side_effect_widget(), "naive-side-effect");
		
		getjopjop_bcmenabled_widget().getButton().addSelectionListener(this);
		
		getjopjop_bcmnaive_side_effect_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.lcm");
		
		
		addToEnableGroup("jop", "jop.lcm", getjopjop_lcmenabled_widget(), "enabled");
		
		addToEnableGroup("jop", "jop.lcm", getjopjop_lcmsafety_widget(), "safety");
		
		addToEnableGroup("jop", "jop.lcm", getjopjop_lcmunroll_widget(), "unroll");
		
		addToEnableGroup("jop", "jop.lcm", getjopjop_lcmnaive_side_effect_widget(), "naive-side-effect");
		
		getjopjop_lcmenabled_widget().getButton().addSelectionListener(this);
		
		getjopjop_lcmunroll_widget().getButton().addSelectionListener(this);
		
		getjopjop_lcmnaive_side_effect_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.cp");
		
		
		addToEnableGroup("jop", "jop.cp", getjopjop_cpenabled_widget(), "enabled");
		
		addToEnableGroup("jop", "jop.cp", getjopjop_cponly_regular_locals_widget(), "only-regular-locals");
		
		addToEnableGroup("jop", "jop.cp", getjopjop_cponly_stack_locals_widget(), "only-stack-locals");
		
		getjopjop_cpenabled_widget().getButton().addSelectionListener(this);
		
		getjopjop_cponly_regular_locals_widget().getButton().addSelectionListener(this);
		
		getjopjop_cponly_stack_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.cpf");
		
		
		addToEnableGroup("jop", "jop.cpf", getjopjop_cpfenabled_widget(), "enabled");
		
		getjopjop_cpfenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.cbf");
		
		
		addToEnableGroup("jop", "jop.cbf", getjopjop_cbfenabled_widget(), "enabled");
		
		getjopjop_cbfenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.dae");
		
		
		addToEnableGroup("jop", "jop.dae", getjopjop_daeenabled_widget(), "enabled");
		
		addToEnableGroup("jop", "jop.dae", getjopjop_daeonly_stack_locals_widget(), "only-stack-locals");
		
		getjopjop_daeenabled_widget().getButton().addSelectionListener(this);
		
		getjopjop_daeonly_stack_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.uce1");
		
		
		addToEnableGroup("jop", "jop.uce1", getjopjop_uce1enabled_widget(), "enabled");
		
		getjopjop_uce1enabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.ubf1");
		
		
		addToEnableGroup("jop", "jop.ubf1", getjopjop_ubf1enabled_widget(), "enabled");
		
		getjopjop_ubf1enabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.uce2");
		
		
		addToEnableGroup("jop", "jop.uce2", getjopjop_uce2enabled_widget(), "enabled");
		
		getjopjop_uce2enabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.ubf2");
		
		
		addToEnableGroup("jop", "jop.ubf2", getjopjop_ubf2enabled_widget(), "enabled");
		
		getjopjop_ubf2enabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jop", "jop.ule");
		
		
		addToEnableGroup("jop", "jop.ule", getjopjop_uleenabled_widget(), "enabled");
		
		getjopjop_uleenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jap");
		
		
		addToEnableGroup("jap", getjapenabled_widget(), "enabled");
		
		
		getjapenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jap", "jap.npc");
		
		
		addToEnableGroup("jap", "jap.npc", getjapjap_npcenabled_widget(), "enabled");
		
		addToEnableGroup("jap", "jap.npc", getjapjap_npconly_array_ref_widget(), "only-array-ref");
		
		addToEnableGroup("jap", "jap.npc", getjapjap_npcprofiling_widget(), "profiling");
		
		getjapjap_npcenabled_widget().getButton().addSelectionListener(this);
		
		getjapjap_npconly_array_ref_widget().getButton().addSelectionListener(this);
		
		getjapjap_npcprofiling_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jap", "jap.abc");
		
		
		addToEnableGroup("jap", "jap.abc", getjapjap_abcenabled_widget(), "enabled");
		
		addToEnableGroup("jap", "jap.abc", getjapjap_abcwith_all_widget(), "with-all");
		
		addToEnableGroup("jap", "jap.abc", getjapjap_abcwith_fieldref_widget(), "with-fieldref");
		
		addToEnableGroup("jap", "jap.abc", getjapjap_abcwith_arrayref_widget(), "with-arrayref");
		
		addToEnableGroup("jap", "jap.abc", getjapjap_abcwith_cse_widget(), "with-cse");
		
		addToEnableGroup("jap", "jap.abc", getjapjap_abcwith_classfield_widget(), "with-classfield");
		
		addToEnableGroup("jap", "jap.abc", getjapjap_abcwith_rectarray_widget(), "with-rectarray");
		
		addToEnableGroup("jap", "jap.abc", getjapjap_abcprofiling_widget(), "profiling");
		
		getjapjap_abcenabled_widget().getButton().addSelectionListener(this);
		
		getjapjap_abcwith_all_widget().getButton().addSelectionListener(this);
		
		getjapjap_abcwith_fieldref_widget().getButton().addSelectionListener(this);
		
		getjapjap_abcwith_arrayref_widget().getButton().addSelectionListener(this);
		
		getjapjap_abcwith_cse_widget().getButton().addSelectionListener(this);
		
		getjapjap_abcwith_classfield_widget().getButton().addSelectionListener(this);
		
		getjapjap_abcwith_rectarray_widget().getButton().addSelectionListener(this);
		
		getjapjap_abcprofiling_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jap", "jap.profiling");
		
		
		addToEnableGroup("jap", "jap.profiling", getjapjap_profilingenabled_widget(), "enabled");
		
		addToEnableGroup("jap", "jap.profiling", getjapjap_profilingnotmainentry_widget(), "notmainentry");
		
		getjapjap_profilingenabled_widget().getButton().addSelectionListener(this);
		
		getjapjap_profilingnotmainentry_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jap", "jap.sea");
		
		
		addToEnableGroup("jap", "jap.sea", getjapjap_seaenabled_widget(), "enabled");
		
		addToEnableGroup("jap", "jap.sea", getjapjap_seanaive_widget(), "naive");
		
		getjapjap_seaenabled_widget().getButton().addSelectionListener(this);
		
		getjapjap_seanaive_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jap", "jap.fieldrw");
		
		
		addToEnableGroup("jap", "jap.fieldrw", getjapjap_fieldrwenabled_widget(), "enabled");
		
		addToEnableGroup("jap", "jap.fieldrw", getjapjap_fieldrwthreshold_widget(), "threshold");
		
		getjapjap_fieldrwenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("jap", "jap.cgtagger");
		
		
		addToEnableGroup("jap", "jap.cgtagger", getjapjap_cgtaggerenabled_widget(), "enabled");
		
		getjapjap_cgtaggerenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("gb");
		
		
		addToEnableGroup("gb", getgbenabled_widget(), "enabled");
		
		
		getgbenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("gb", "gb.a1");
		
		
		addToEnableGroup("gb", "gb.a1", getgbgb_a1enabled_widget(), "enabled");
		
		addToEnableGroup("gb", "gb.a1", getgbgb_a1only_stack_locals_widget(), "only-stack-locals");
		
		getgbgb_a1enabled_widget().getButton().addSelectionListener(this);
		
		getgbgb_a1only_stack_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("gb", "gb.cf");
		
		
		addToEnableGroup("gb", "gb.cf", getgbgb_cfenabled_widget(), "enabled");
		
		getgbgb_cfenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("gb", "gb.a2");
		
		
		addToEnableGroup("gb", "gb.a2", getgbgb_a2enabled_widget(), "enabled");
		
		addToEnableGroup("gb", "gb.a2", getgbgb_a2only_stack_locals_widget(), "only-stack-locals");
		
		getgbgb_a2enabled_widget().getButton().addSelectionListener(this);
		
		getgbgb_a2only_stack_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("gb", "gb.ule");
		
		
		addToEnableGroup("gb", "gb.ule", getgbgb_uleenabled_widget(), "enabled");
		
		getgbgb_uleenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("gop");
		
		
		addToEnableGroup("gop", getgopenabled_widget(), "enabled");
		
		
		getgopenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("bb");
		
		
		addToEnableGroup("bb", getbbenabled_widget(), "enabled");
		
		
		getbbenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("bb", "bb.lso");
		
		
		addToEnableGroup("bb", "bb.lso", getbbbb_lsoenabled_widget(), "enabled");
		
		addToEnableGroup("bb", "bb.lso", getbbbb_lsodebug_widget(), "debug");
		
		addToEnableGroup("bb", "bb.lso", getbbbb_lsointer_widget(), "inter");
		
		addToEnableGroup("bb", "bb.lso", getbbbb_lsosl_widget(), "sl");
		
		addToEnableGroup("bb", "bb.lso", getbbbb_lsosl2_widget(), "sl2");
		
		addToEnableGroup("bb", "bb.lso", getbbbb_lsosll_widget(), "sll");
		
		addToEnableGroup("bb", "bb.lso", getbbbb_lsosll2_widget(), "sll2");
		
		getbbbb_lsoenabled_widget().getButton().addSelectionListener(this);
		
		getbbbb_lsodebug_widget().getButton().addSelectionListener(this);
		
		getbbbb_lsointer_widget().getButton().addSelectionListener(this);
		
		getbbbb_lsosl_widget().getButton().addSelectionListener(this);
		
		getbbbb_lsosl2_widget().getButton().addSelectionListener(this);
		
		getbbbb_lsosll_widget().getButton().addSelectionListener(this);
		
		getbbbb_lsosll2_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("bb", "bb.pho");
		
		
		addToEnableGroup("bb", "bb.pho", getbbbb_phoenabled_widget(), "enabled");
		
		getbbbb_phoenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("bb", "bb.ule");
		
		
		addToEnableGroup("bb", "bb.ule", getbbbb_uleenabled_widget(), "enabled");
		
		getbbbb_uleenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("bb", "bb.lp");
		
		
		addToEnableGroup("bb", "bb.lp", getbbbb_lpenabled_widget(), "enabled");
		
		addToEnableGroup("bb", "bb.lp", getbbbb_lpunsplit_original_locals_widget(), "unsplit-original-locals");
		
		getbbbb_lpenabled_widget().getButton().addSelectionListener(this);
		
		getbbbb_lpunsplit_original_locals_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("bop");
		
		
		addToEnableGroup("bop", getbopenabled_widget(), "enabled");
		
		
		getbopenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("tag");
		
		
		addToEnableGroup("tag", gettagenabled_widget(), "enabled");
		
		
		gettagenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("tag", "tag.ln");
		
		
		addToEnableGroup("tag", "tag.ln", gettagtag_lnenabled_widget(), "enabled");
		
		gettagtag_lnenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("tag", "tag.an");
		
		
		addToEnableGroup("tag", "tag.an", gettagtag_anenabled_widget(), "enabled");
		
		gettagtag_anenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("tag", "tag.dep");
		
		
		addToEnableGroup("tag", "tag.dep", gettagtag_depenabled_widget(), "enabled");
		
		gettagtag_depenabled_widget().getButton().addSelectionListener(this);
		
		
		makeNewEnableGroup("tag", "tag.fieldrw");
		
		
		addToEnableGroup("tag", "tag.fieldrw", gettagtag_fieldrwenabled_widget(), "enabled");
		
		gettagtag_fieldrwenabled_widget().getButton().addSelectionListener(this);
		

		updateAllEnableGroups();
	}
	
	public void widgetSelected(SelectionEvent e){
		handleWidgetSelected(e);
	}

	public void widgetDefaultSelected(SelectionEvent e){
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
		StringTokenizer listOptTokens;
		String nextListToken;
	
		
		boolRes = getGeneral_Optionshelp_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getGeneral_Optionshelp_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getGeneral_Optionsphase_list_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getGeneral_Optionsphase_list_widget().getAlias(), new Boolean(boolRes));
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
		
		boolRes = getGeneral_Optionsdebug_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getGeneral_Optionsdebug_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getGeneral_Optionsphase_help_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getGeneral_Optionsphase_help_widget().getAlias(), stringRes);
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
		
		boolRes = getjbenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbuse_original_names_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbuse_original_names_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lsenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lsenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_aenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_aenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_aonly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_aonly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_uleenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_uleenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_trenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_trenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_ulpenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_ulpenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_ulpunsplit_original_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_ulpunsplit_original_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lnsenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lnsenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lnsonly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lnsonly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_cpenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_cpenabled_widget().getAlias(), new Boolean(boolRes));
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
		
		boolRes = getjbjb_daeenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_daeenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_daeonly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_daeonly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_cp_uleenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_cp_uleenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lpenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lpenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_lpunsplit_original_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_lpunsplit_original_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_neenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_neenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjbjb_uceenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjbjb_uceenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgsafe_forname_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgsafe_forname_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgverbose_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgverbose_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_chaenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_chaenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_chaverbose_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_chaverbose_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkenabled_widget().getAlias(), new Boolean(boolRes));
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
		
		
		defBoolRes = true;
		

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
		
		boolRes = getcgcg_sparkadd_tags_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkadd_tags_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getcgcg_sparkset_mass_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getcgcg_sparkset_mass_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjtpenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjtpenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjopenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjopwjop_smbenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopwjop_smbenabled_widget().getAlias(), new Boolean(boolRes));
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
		
		boolRes = getwjopwjop_sienabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjopwjop_sienabled_widget().getAlias(), new Boolean(boolRes));
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
		
		boolRes = getwjapenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjapenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getwjapwjap_raenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getwjapwjap_raenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getshimpleenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getshimpleenabled_widget().getAlias(), new Boolean(boolRes));
		}
		 
		stringRes = getshimplephi_elim_opt_widget().getSelectedAlias();

		
		defStringRes = "post";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getshimplephi_elim_opt_widget().getAlias(), stringRes);
		}
		
		boolRes = getstpenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getstpenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getsopenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getsopenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getsopsop_cpfenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getsopsop_cpfenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjtpenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjtpenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_cseenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_cseenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_csenaive_side_effect_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_csenaive_side_effect_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_bcmenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_bcmenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_bcmnaive_side_effect_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_bcmnaive_side_effect_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_lcmenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_lcmenabled_widget().getAlias(), new Boolean(boolRes));
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
		 
		stringRes = getjopjop_lcmsafety_widget().getSelectedAlias();

		
		defStringRes = "safe";
		

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(getjopjop_lcmsafety_widget().getAlias(), stringRes);
		}
		
		boolRes = getjopjop_cpenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_cpenabled_widget().getAlias(), new Boolean(boolRes));
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
		
		boolRes = getjopjop_cpfenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_cpfenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_cbfenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_cbfenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_daeenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_daeenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_daeonly_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_daeonly_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_uce1enabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_uce1enabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_ubf1enabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_ubf1enabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_uce2enabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_uce2enabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_ubf2enabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_ubf2enabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjopjop_uleenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjopjop_uleenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_npcenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_npcenabled_widget().getAlias(), new Boolean(boolRes));
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
		
		boolRes = getjapjap_abcenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_abcenabled_widget().getAlias(), new Boolean(boolRes));
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
		
		boolRes = getjapjap_profilingenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_profilingenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_profilingnotmainentry_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_profilingnotmainentry_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_seaenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_seaenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_seanaive_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_seanaive_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getjapjap_fieldrwenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_fieldrwenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getjapjap_fieldrwthreshold_widget().getText().getText();
		
		defStringRes = "100";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getjapjap_fieldrwthreshold_widget().getAlias(), stringRes);
		}
		
		boolRes = getjapjap_cgtaggerenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getjapjap_cgtaggerenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_a1enabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_a1enabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_a1only_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_a1only_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_cfenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_cfenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_a2enabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_a2enabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_a2only_stack_locals_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_a2only_stack_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgbgb_uleenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgbgb_uleenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getgopenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getgopenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lsoenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lsoenabled_widget().getAlias(), new Boolean(boolRes));
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
		
		boolRes = getbbbb_phoenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_phoenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_uleenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_uleenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lpenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lpenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbbbb_lpunsplit_original_locals_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbbbb_lpunsplit_original_locals_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = getbopenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(getbopenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagenabled_widget().getButton().getSelection();
		
		
		defBoolRes = true;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagtag_lnenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagtag_lnenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagtag_anenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagtag_anenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagtag_depenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagtag_depenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		boolRes = gettagtag_fieldrwenabled_widget().getButton().getSelection();
		
		
		defBoolRes = false;
		

		if (boolRes != defBoolRes) {
			getConfig().put(gettagtag_fieldrwenabled_widget().getAlias(), new Boolean(boolRes));
		}
		
		stringRes = getSingle_File_Mode_Optionsprocess_path_widget().getText().getText();
		
		defStringRes = "";
		

	        if ( (!(stringRes.equals(defStringRes))) && (stringRes != null) && (stringRes.length() != 0)) {
			getConfig().put(getSingle_File_Mode_Optionsprocess_path_widget().getAlias(), stringRes);
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
		SootOption root = new SootOption("", "");
		SootOption parent;
		SootOption subParent;
		SootOption subSectParent;
		

		SootOption General_Options_branch = new SootOption("General Options", "General_Options");
		root.addChild(General_Options_branch);
		parent = General_Options_branch;		
		
		SootOption Input_Options_branch = new SootOption("Input Options", "Input_Options");
		root.addChild(Input_Options_branch);
		parent = Input_Options_branch;		
		
		SootOption Output_Options_branch = new SootOption("Output Options", "Output_Options");
		root.addChild(Output_Options_branch);
		parent = Output_Options_branch;		
		
		SootOption Processing_Options_branch = new SootOption("Processing Options", "Processing_Options");
		root.addChild(Processing_Options_branch);
		parent = Processing_Options_branch;		
		
		SootOption Phase_Options_branch = new SootOption("Phase Options", "p");
		root.addChild(Phase_Options_branch);

		parent = Phase_Options_branch;	

		
		
		//Phase Options
			//Jimple Body Creation
			SootOption jb_branch = new SootOption("Jimple Body Creation", "jb");
			parent.addChild(jb_branch);
			subParent = jb_branch;


			
			SootOption jb_jb_ls_branch = new SootOption("Local Splitter", "jbjb_ls");
			subParent.addChild(jb_jb_ls_branch);


			

			
			subSectParent = jb_jb_ls_branch;
			
			
			SootOption jb_jb_a_branch = new SootOption("Jimple Local Aggregator", "jbjb_a");
			subParent.addChild(jb_jb_a_branch);


			

			
			subSectParent = jb_jb_a_branch;
			
			
			SootOption jb_jb_ule_branch = new SootOption("Unused Local Eliminator", "jbjb_ule");
			subParent.addChild(jb_jb_ule_branch);


			

			
			subSectParent = jb_jb_ule_branch;
			
			
			SootOption jb_jb_tr_branch = new SootOption("Type Assigner", "jbjb_tr");
			subParent.addChild(jb_jb_tr_branch);


			

			
			subSectParent = jb_jb_tr_branch;
			
			
			SootOption jb_jb_ulp_branch = new SootOption("Unsplit-originals Local Packer", "jbjb_ulp");
			subParent.addChild(jb_jb_ulp_branch);


			

			
			subSectParent = jb_jb_ulp_branch;
			
			
			SootOption jb_jb_lns_branch = new SootOption("Local Name Standardizer", "jbjb_lns");
			subParent.addChild(jb_jb_lns_branch);


			

			
			subSectParent = jb_jb_lns_branch;
			
			
			SootOption jb_jb_cp_branch = new SootOption("Copy Propagator", "jbjb_cp");
			subParent.addChild(jb_jb_cp_branch);


			

			
			subSectParent = jb_jb_cp_branch;
			
			
			SootOption jb_jb_dae_branch = new SootOption("Dead Assignment Eliminator", "jbjb_dae");
			subParent.addChild(jb_jb_dae_branch);


			

			
			subSectParent = jb_jb_dae_branch;
			
			
			SootOption jb_jb_cp_ule_branch = new SootOption("Post-copy propagation Unused Local Eliminator", "jbjb_cp_ule");
			subParent.addChild(jb_jb_cp_ule_branch);


			

			
			subSectParent = jb_jb_cp_ule_branch;
			
			
			SootOption jb_jb_lp_branch = new SootOption("Local Packer", "jbjb_lp");
			subParent.addChild(jb_jb_lp_branch);


			

			
			subSectParent = jb_jb_lp_branch;
			
			
			SootOption jb_jb_ne_branch = new SootOption("Nop Eliminator", "jbjb_ne");
			subParent.addChild(jb_jb_ne_branch);


			

			
			subSectParent = jb_jb_ne_branch;
			
			
			SootOption jb_jb_uce_branch = new SootOption("Unreachable Code Eliminator", "jbjb_uce");
			subParent.addChild(jb_jb_uce_branch);


			

			
			subSectParent = jb_jb_uce_branch;
			
			
			//Call Graph
			SootOption cg_branch = new SootOption("Call Graph", "cg");
			parent.addChild(cg_branch);
			subParent = cg_branch;


			
			SootOption cg_cg_cha_branch = new SootOption("Class Hierarchy Analysis", "cgcg_cha");
			subParent.addChild(cg_cg_cha_branch);


			

			
			subSectParent = cg_cg_cha_branch;
			
			
			SootOption cg_cg_spark_branch = new SootOption("Spark", "cgcg_spark");
			subParent.addChild(cg_cg_spark_branch);


			

			
			subSectParent = cg_cg_spark_branch;
			
			
			SootOption cg_Spark_General_Options_branch = new SootOption("Spark General Options", "cgSpark_General_Options");

			subSectParent.addChild(cg_Spark_General_Options_branch);
			
			SootOption cg_Spark_Pointer_Assignment_Graph_Building_Options_branch = new SootOption("Spark Pointer Assignment Graph Building Options", "cgSpark_Pointer_Assignment_Graph_Building_Options");

			subSectParent.addChild(cg_Spark_Pointer_Assignment_Graph_Building_Options_branch);
			
			SootOption cg_Spark_Pointer_Assignment_Graph_Simplification_Options_branch = new SootOption("Spark Pointer Assignment Graph Simplification Options", "cgSpark_Pointer_Assignment_Graph_Simplification_Options");

			subSectParent.addChild(cg_Spark_Pointer_Assignment_Graph_Simplification_Options_branch);
			
			SootOption cg_Spark_Points_To_Set_Flowing_Options_branch = new SootOption("Spark Points-To Set Flowing Options", "cgSpark_Points_To_Set_Flowing_Options");

			subSectParent.addChild(cg_Spark_Points_To_Set_Flowing_Options_branch);
			
			SootOption cg_Spark_Output_Options_branch = new SootOption("Spark Output Options", "cgSpark_Output_Options");

			subSectParent.addChild(cg_Spark_Output_Options_branch);
			
			//Whole-Jimple Transformation Pack
			SootOption wjtp_branch = new SootOption("Whole-Jimple Transformation Pack", "wjtp");
			parent.addChild(wjtp_branch);
			subParent = wjtp_branch;


			
			//Whole-Jimple Optimization Pack
			SootOption wjop_branch = new SootOption("Whole-Jimple Optimization Pack", "wjop");
			parent.addChild(wjop_branch);
			subParent = wjop_branch;


			
			SootOption wjop_wjop_smb_branch = new SootOption("Static Method Binding", "wjopwjop_smb");
			subParent.addChild(wjop_wjop_smb_branch);


			

			
			subSectParent = wjop_wjop_smb_branch;
			
			
			SootOption wjop_wjop_si_branch = new SootOption("Static Inlining", "wjopwjop_si");
			subParent.addChild(wjop_wjop_si_branch);


			

			
			subSectParent = wjop_wjop_si_branch;
			
			
			//Whole-Jimple Annotation Pack
			SootOption wjap_branch = new SootOption("Whole-Jimple Annotation Pack", "wjap");
			parent.addChild(wjap_branch);
			subParent = wjap_branch;


			
			SootOption wjap_wjap_ra_branch = new SootOption("Rectangular Array Finder", "wjapwjap_ra");
			subParent.addChild(wjap_wjap_ra_branch);


			

			
			subSectParent = wjap_wjap_ra_branch;
			
			
			//Shimple Phase Options
			SootOption shimple_branch = new SootOption("Shimple Phase Options", "shimple");
			parent.addChild(shimple_branch);
			subParent = shimple_branch;


			
			//Shimple Transformation Pack
			SootOption stp_branch = new SootOption("Shimple Transformation Pack", "stp");
			parent.addChild(stp_branch);
			subParent = stp_branch;


			
			//Shimple Optimization Pack
			SootOption sop_branch = new SootOption("Shimple Optimization Pack", "sop");
			parent.addChild(sop_branch);
			subParent = sop_branch;


			
			SootOption sop_sop_cpf_branch = new SootOption("Constant Propagator and Folder", "sopsop_cpf");
			subParent.addChild(sop_sop_cpf_branch);


			

			
			subSectParent = sop_sop_cpf_branch;
			
			
			//Jimple Transformation Pack
			SootOption jtp_branch = new SootOption("Jimple Transformation Pack", "jtp");
			parent.addChild(jtp_branch);
			subParent = jtp_branch;


			
			//Jimple Optimization Pack
			SootOption jop_branch = new SootOption("Jimple Optimization Pack", "jop");
			parent.addChild(jop_branch);
			subParent = jop_branch;


			
			SootOption jop_jop_cse_branch = new SootOption("Common Subexpression Eliminator", "jopjop_cse");
			subParent.addChild(jop_jop_cse_branch);


			

			
			subSectParent = jop_jop_cse_branch;
			
			
			SootOption jop_jop_bcm_branch = new SootOption("Busy Code Motion", "jopjop_bcm");
			subParent.addChild(jop_jop_bcm_branch);


			

			
			subSectParent = jop_jop_bcm_branch;
			
			
			SootOption jop_jop_lcm_branch = new SootOption("Lazy Code Motion", "jopjop_lcm");
			subParent.addChild(jop_jop_lcm_branch);


			

			
			subSectParent = jop_jop_lcm_branch;
			
			
			SootOption jop_jop_cp_branch = new SootOption("Copy Propagator", "jopjop_cp");
			subParent.addChild(jop_jop_cp_branch);


			

			
			subSectParent = jop_jop_cp_branch;
			
			
			SootOption jop_jop_cpf_branch = new SootOption("Constant Propagator and Folder", "jopjop_cpf");
			subParent.addChild(jop_jop_cpf_branch);


			

			
			subSectParent = jop_jop_cpf_branch;
			
			
			SootOption jop_jop_cbf_branch = new SootOption("Conditional Branch Folder", "jopjop_cbf");
			subParent.addChild(jop_jop_cbf_branch);


			

			
			subSectParent = jop_jop_cbf_branch;
			
			
			SootOption jop_jop_dae_branch = new SootOption("Dead Assignment Eliminator", "jopjop_dae");
			subParent.addChild(jop_jop_dae_branch);


			

			
			subSectParent = jop_jop_dae_branch;
			
			
			SootOption jop_jop_uce1_branch = new SootOption("Unreachable Code Eliminator 1", "jopjop_uce1");
			subParent.addChild(jop_jop_uce1_branch);


			

			
			subSectParent = jop_jop_uce1_branch;
			
			
			SootOption jop_jop_ubf1_branch = new SootOption("Unconditional Branch Folder 1", "jopjop_ubf1");
			subParent.addChild(jop_jop_ubf1_branch);


			

			
			subSectParent = jop_jop_ubf1_branch;
			
			
			SootOption jop_jop_uce2_branch = new SootOption("Unreachable Code Eliminator 2", "jopjop_uce2");
			subParent.addChild(jop_jop_uce2_branch);


			

			
			subSectParent = jop_jop_uce2_branch;
			
			
			SootOption jop_jop_ubf2_branch = new SootOption("Unconditional Branch Folder 2", "jopjop_ubf2");
			subParent.addChild(jop_jop_ubf2_branch);


			

			
			subSectParent = jop_jop_ubf2_branch;
			
			
			SootOption jop_jop_ule_branch = new SootOption("Unused Local Eliminator", "jopjop_ule");
			subParent.addChild(jop_jop_ule_branch);


			

			
			subSectParent = jop_jop_ule_branch;
			
			
			//Jimple Annotation Pack
			SootOption jap_branch = new SootOption("Jimple Annotation Pack", "jap");
			parent.addChild(jap_branch);
			subParent = jap_branch;


			
			SootOption jap_jap_npc_branch = new SootOption("Null Pointer Check Options", "japjap_npc");
			subParent.addChild(jap_jap_npc_branch);


			

			
			subSectParent = jap_jap_npc_branch;
			
			
			SootOption jap_jap_abc_branch = new SootOption("Array Bound Check Options", "japjap_abc");
			subParent.addChild(jap_jap_abc_branch);


			

			
			subSectParent = jap_jap_abc_branch;
			
			
			SootOption jap_jap_profiling_branch = new SootOption("Profiling Generator", "japjap_profiling");
			subParent.addChild(jap_jap_profiling_branch);


			

			
			subSectParent = jap_jap_profiling_branch;
			
			
			SootOption jap_jap_sea_branch = new SootOption("Side effect tagger", "japjap_sea");
			subParent.addChild(jap_jap_sea_branch);


			

			
			subSectParent = jap_jap_sea_branch;
			
			
			SootOption jap_jap_fieldrw_branch = new SootOption("Field Read/Write Tagger", "japjap_fieldrw");
			subParent.addChild(jap_jap_fieldrw_branch);


			

			
			subSectParent = jap_jap_fieldrw_branch;
			
			
			SootOption jap_jap_cgtagger_branch = new SootOption("Call Graph Tagger", "japjap_cgtagger");
			subParent.addChild(jap_jap_cgtagger_branch);


			

			
			subSectParent = jap_jap_cgtagger_branch;
			
			
			//Grimp Body Creation
			SootOption gb_branch = new SootOption("Grimp Body Creation", "gb");
			parent.addChild(gb_branch);
			subParent = gb_branch;


			
			SootOption gb_gb_a1_branch = new SootOption("Grimp Pre-folding Aggregator", "gbgb_a1");
			subParent.addChild(gb_gb_a1_branch);


			

			
			subSectParent = gb_gb_a1_branch;
			
			
			SootOption gb_gb_cf_branch = new SootOption("Grimp Constructor Folder", "gbgb_cf");
			subParent.addChild(gb_gb_cf_branch);


			

			
			subSectParent = gb_gb_cf_branch;
			
			
			SootOption gb_gb_a2_branch = new SootOption("Grimp Post-folding Aggregator", "gbgb_a2");
			subParent.addChild(gb_gb_a2_branch);


			

			
			subSectParent = gb_gb_a2_branch;
			
			
			SootOption gb_gb_ule_branch = new SootOption("Grimp Unused Local Eliminator", "gbgb_ule");
			subParent.addChild(gb_gb_ule_branch);


			

			
			subSectParent = gb_gb_ule_branch;
			
			
			//Grimp Optimization
			SootOption gop_branch = new SootOption("Grimp Optimization", "gop");
			parent.addChild(gop_branch);
			subParent = gop_branch;


			
			//Baf Body Creation
			SootOption bb_branch = new SootOption("Baf Body Creation", "bb");
			parent.addChild(bb_branch);
			subParent = bb_branch;


			
			SootOption bb_bb_lso_branch = new SootOption("Load Store Optimizer", "bbbb_lso");
			subParent.addChild(bb_bb_lso_branch);


			

			
			subSectParent = bb_bb_lso_branch;
			
			
			SootOption bb_bb_pho_branch = new SootOption("Peephole Optimizer", "bbbb_pho");
			subParent.addChild(bb_bb_pho_branch);


			

			
			subSectParent = bb_bb_pho_branch;
			
			
			SootOption bb_bb_ule_branch = new SootOption("Unused Local Eliminator", "bbbb_ule");
			subParent.addChild(bb_bb_ule_branch);


			

			
			subSectParent = bb_bb_ule_branch;
			
			
			SootOption bb_bb_lp_branch = new SootOption("Local Packer", "bbbb_lp");
			subParent.addChild(bb_bb_lp_branch);


			

			
			subSectParent = bb_bb_lp_branch;
			
			
			//Baf Optimization
			SootOption bop_branch = new SootOption("Baf Optimization", "bop");
			parent.addChild(bop_branch);
			subParent = bop_branch;


			
			//Tag
			SootOption tag_branch = new SootOption("Tag", "tag");
			parent.addChild(tag_branch);
			subParent = tag_branch;


			
			SootOption tag_tag_ln_branch = new SootOption("Line Number Tag Aggregator", "tagtag_ln");
			subParent.addChild(tag_tag_ln_branch);


			

			
			subSectParent = tag_tag_ln_branch;
			
			
			SootOption tag_tag_an_branch = new SootOption("Array Bounds and Null Pointer Check Tag Aggregator", "tagtag_an");
			subParent.addChild(tag_tag_an_branch);


			

			
			subSectParent = tag_tag_an_branch;
			
			
			SootOption tag_tag_dep_branch = new SootOption("Dependence Tag Aggregator", "tagtag_dep");
			subParent.addChild(tag_tag_dep_branch);


			

			
			subSectParent = tag_tag_dep_branch;
			
			
			SootOption tag_tag_fieldrw_branch = new SootOption("Field Read/Write Tag Aggregator", "tagtag_fieldrw");
			subParent.addChild(tag_tag_fieldrw_branch);


			

			
			subSectParent = tag_tag_fieldrw_branch;
			
			
		SootOption Single_File_Mode_Options_branch = new SootOption("Single File Mode Options", "Single_File_Mode_Options");
		root.addChild(Single_File_Mode_Options_branch);
		parent = Single_File_Mode_Options_branch;		
		
		SootOption Application_Mode_Options_branch = new SootOption("Application Mode Options", "Application_Mode_Options");
		root.addChild(Application_Mode_Options_branch);
		parent = Application_Mode_Options_branch;		
		
		SootOption Input_Attribute_Options_branch = new SootOption("Input Attribute Options", "Input_Attribute_Options");
		root.addChild(Input_Attribute_Options_branch);
		parent = Input_Attribute_Options_branch;		
		
		SootOption Annotation_Options_branch = new SootOption("Annotation Options", "Annotation_Options");
		root.addChild(Annotation_Options_branch);
		parent = Annotation_Options_branch;		
		
		SootOption Miscellaneous_Options_branch = new SootOption("Miscellaneous Options", "Miscellaneous_Options");
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
	
	private BooleanOptionWidget General_Optionsphase_list_widget;
	
	private void setGeneral_Optionsphase_list_widget(BooleanOptionWidget widget) {
		General_Optionsphase_list_widget = widget;
	}
	
	public BooleanOptionWidget getGeneral_Optionsphase_list_widget() {
		return General_Optionsphase_list_widget;
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
	
	private BooleanOptionWidget General_Optionsdebug_widget;
	
	private void setGeneral_Optionsdebug_widget(BooleanOptionWidget widget) {
		General_Optionsdebug_widget = widget;
	}
	
	public BooleanOptionWidget getGeneral_Optionsdebug_widget() {
		return General_Optionsdebug_widget;
	}	
	

	private ListOptionWidget General_Optionsphase_help_widget;
	
	private void setGeneral_Optionsphase_help_widget(ListOptionWidget widget) {
		General_Optionsphase_help_widget = widget;
	}
	
	public ListOptionWidget getGeneral_Optionsphase_help_widget() {
		return General_Optionsphase_help_widget;
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
	
	private BooleanOptionWidget jbenabled_widget;
	
	private void setjbenabled_widget(BooleanOptionWidget widget) {
		jbenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbenabled_widget() {
		return jbenabled_widget;
	}	
	
	private BooleanOptionWidget jbuse_original_names_widget;
	
	private void setjbuse_original_names_widget(BooleanOptionWidget widget) {
		jbuse_original_names_widget = widget;
	}
	
	public BooleanOptionWidget getjbuse_original_names_widget() {
		return jbuse_original_names_widget;
	}	
	
	private BooleanOptionWidget jbjb_lsenabled_widget;
	
	private void setjbjb_lsenabled_widget(BooleanOptionWidget widget) {
		jbjb_lsenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lsenabled_widget() {
		return jbjb_lsenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_aenabled_widget;
	
	private void setjbjb_aenabled_widget(BooleanOptionWidget widget) {
		jbjb_aenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_aenabled_widget() {
		return jbjb_aenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_aonly_stack_locals_widget;
	
	private void setjbjb_aonly_stack_locals_widget(BooleanOptionWidget widget) {
		jbjb_aonly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_aonly_stack_locals_widget() {
		return jbjb_aonly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_uleenabled_widget;
	
	private void setjbjb_uleenabled_widget(BooleanOptionWidget widget) {
		jbjb_uleenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_uleenabled_widget() {
		return jbjb_uleenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_trenabled_widget;
	
	private void setjbjb_trenabled_widget(BooleanOptionWidget widget) {
		jbjb_trenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_trenabled_widget() {
		return jbjb_trenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_ulpenabled_widget;
	
	private void setjbjb_ulpenabled_widget(BooleanOptionWidget widget) {
		jbjb_ulpenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_ulpenabled_widget() {
		return jbjb_ulpenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_ulpunsplit_original_locals_widget;
	
	private void setjbjb_ulpunsplit_original_locals_widget(BooleanOptionWidget widget) {
		jbjb_ulpunsplit_original_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_ulpunsplit_original_locals_widget() {
		return jbjb_ulpunsplit_original_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_lnsenabled_widget;
	
	private void setjbjb_lnsenabled_widget(BooleanOptionWidget widget) {
		jbjb_lnsenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lnsenabled_widget() {
		return jbjb_lnsenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_lnsonly_stack_locals_widget;
	
	private void setjbjb_lnsonly_stack_locals_widget(BooleanOptionWidget widget) {
		jbjb_lnsonly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lnsonly_stack_locals_widget() {
		return jbjb_lnsonly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_cpenabled_widget;
	
	private void setjbjb_cpenabled_widget(BooleanOptionWidget widget) {
		jbjb_cpenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_cpenabled_widget() {
		return jbjb_cpenabled_widget;
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
	
	private BooleanOptionWidget jbjb_daeenabled_widget;
	
	private void setjbjb_daeenabled_widget(BooleanOptionWidget widget) {
		jbjb_daeenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_daeenabled_widget() {
		return jbjb_daeenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_daeonly_stack_locals_widget;
	
	private void setjbjb_daeonly_stack_locals_widget(BooleanOptionWidget widget) {
		jbjb_daeonly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_daeonly_stack_locals_widget() {
		return jbjb_daeonly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_cp_uleenabled_widget;
	
	private void setjbjb_cp_uleenabled_widget(BooleanOptionWidget widget) {
		jbjb_cp_uleenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_cp_uleenabled_widget() {
		return jbjb_cp_uleenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_lpenabled_widget;
	
	private void setjbjb_lpenabled_widget(BooleanOptionWidget widget) {
		jbjb_lpenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lpenabled_widget() {
		return jbjb_lpenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_lpunsplit_original_locals_widget;
	
	private void setjbjb_lpunsplit_original_locals_widget(BooleanOptionWidget widget) {
		jbjb_lpunsplit_original_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_lpunsplit_original_locals_widget() {
		return jbjb_lpunsplit_original_locals_widget;
	}	
	
	private BooleanOptionWidget jbjb_neenabled_widget;
	
	private void setjbjb_neenabled_widget(BooleanOptionWidget widget) {
		jbjb_neenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_neenabled_widget() {
		return jbjb_neenabled_widget;
	}	
	
	private BooleanOptionWidget jbjb_uceenabled_widget;
	
	private void setjbjb_uceenabled_widget(BooleanOptionWidget widget) {
		jbjb_uceenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjbjb_uceenabled_widget() {
		return jbjb_uceenabled_widget;
	}	
	
	private BooleanOptionWidget cgenabled_widget;
	
	private void setcgenabled_widget(BooleanOptionWidget widget) {
		cgenabled_widget = widget;
	}
	
	public BooleanOptionWidget getcgenabled_widget() {
		return cgenabled_widget;
	}	
	
	private BooleanOptionWidget cgsafe_forname_widget;
	
	private void setcgsafe_forname_widget(BooleanOptionWidget widget) {
		cgsafe_forname_widget = widget;
	}
	
	public BooleanOptionWidget getcgsafe_forname_widget() {
		return cgsafe_forname_widget;
	}	
	
	private BooleanOptionWidget cgverbose_widget;
	
	private void setcgverbose_widget(BooleanOptionWidget widget) {
		cgverbose_widget = widget;
	}
	
	public BooleanOptionWidget getcgverbose_widget() {
		return cgverbose_widget;
	}	
	
	private BooleanOptionWidget cgcg_chaenabled_widget;
	
	private void setcgcg_chaenabled_widget(BooleanOptionWidget widget) {
		cgcg_chaenabled_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_chaenabled_widget() {
		return cgcg_chaenabled_widget;
	}	
	
	private BooleanOptionWidget cgcg_chaverbose_widget;
	
	private void setcgcg_chaverbose_widget(BooleanOptionWidget widget) {
		cgcg_chaverbose_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_chaverbose_widget() {
		return cgcg_chaverbose_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkenabled_widget;
	
	private void setcgcg_sparkenabled_widget(BooleanOptionWidget widget) {
		cgcg_sparkenabled_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkenabled_widget() {
		return cgcg_sparkenabled_widget;
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
	
	private BooleanOptionWidget cgcg_sparkadd_tags_widget;
	
	private void setcgcg_sparkadd_tags_widget(BooleanOptionWidget widget) {
		cgcg_sparkadd_tags_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkadd_tags_widget() {
		return cgcg_sparkadd_tags_widget;
	}	
	
	private BooleanOptionWidget cgcg_sparkset_mass_widget;
	
	private void setcgcg_sparkset_mass_widget(BooleanOptionWidget widget) {
		cgcg_sparkset_mass_widget = widget;
	}
	
	public BooleanOptionWidget getcgcg_sparkset_mass_widget() {
		return cgcg_sparkset_mass_widget;
	}	
	
	private BooleanOptionWidget wjtpenabled_widget;
	
	private void setwjtpenabled_widget(BooleanOptionWidget widget) {
		wjtpenabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjtpenabled_widget() {
		return wjtpenabled_widget;
	}	
	
	private BooleanOptionWidget wjopenabled_widget;
	
	private void setwjopenabled_widget(BooleanOptionWidget widget) {
		wjopenabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjopenabled_widget() {
		return wjopenabled_widget;
	}	
	
	private BooleanOptionWidget wjopwjop_smbenabled_widget;
	
	private void setwjopwjop_smbenabled_widget(BooleanOptionWidget widget) {
		wjopwjop_smbenabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjopwjop_smbenabled_widget() {
		return wjopwjop_smbenabled_widget;
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
	
	
	private BooleanOptionWidget wjopwjop_sienabled_widget;
	
	private void setwjopwjop_sienabled_widget(BooleanOptionWidget widget) {
		wjopwjop_sienabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjopwjop_sienabled_widget() {
		return wjopwjop_sienabled_widget;
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
	
	
	private BooleanOptionWidget wjapenabled_widget;
	
	private void setwjapenabled_widget(BooleanOptionWidget widget) {
		wjapenabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjapenabled_widget() {
		return wjapenabled_widget;
	}	
	
	private BooleanOptionWidget wjapwjap_raenabled_widget;
	
	private void setwjapwjap_raenabled_widget(BooleanOptionWidget widget) {
		wjapwjap_raenabled_widget = widget;
	}
	
	public BooleanOptionWidget getwjapwjap_raenabled_widget() {
		return wjapwjap_raenabled_widget;
	}	
	
	private BooleanOptionWidget shimpleenabled_widget;
	
	private void setshimpleenabled_widget(BooleanOptionWidget widget) {
		shimpleenabled_widget = widget;
	}
	
	public BooleanOptionWidget getshimpleenabled_widget() {
		return shimpleenabled_widget;
	}	
	
	
	private MultiOptionWidget shimplephi_elim_opt_widget;
	
	private void setshimplephi_elim_opt_widget(MultiOptionWidget widget) {
		shimplephi_elim_opt_widget = widget;
	}
	
	public MultiOptionWidget getshimplephi_elim_opt_widget() {
		return shimplephi_elim_opt_widget;
	}	
	
	
	private BooleanOptionWidget stpenabled_widget;
	
	private void setstpenabled_widget(BooleanOptionWidget widget) {
		stpenabled_widget = widget;
	}
	
	public BooleanOptionWidget getstpenabled_widget() {
		return stpenabled_widget;
	}	
	
	private BooleanOptionWidget sopenabled_widget;
	
	private void setsopenabled_widget(BooleanOptionWidget widget) {
		sopenabled_widget = widget;
	}
	
	public BooleanOptionWidget getsopenabled_widget() {
		return sopenabled_widget;
	}	
	
	private BooleanOptionWidget sopsop_cpfenabled_widget;
	
	private void setsopsop_cpfenabled_widget(BooleanOptionWidget widget) {
		sopsop_cpfenabled_widget = widget;
	}
	
	public BooleanOptionWidget getsopsop_cpfenabled_widget() {
		return sopsop_cpfenabled_widget;
	}	
	
	private BooleanOptionWidget jtpenabled_widget;
	
	private void setjtpenabled_widget(BooleanOptionWidget widget) {
		jtpenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjtpenabled_widget() {
		return jtpenabled_widget;
	}	
	
	private BooleanOptionWidget jopenabled_widget;
	
	private void setjopenabled_widget(BooleanOptionWidget widget) {
		jopenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopenabled_widget() {
		return jopenabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_cseenabled_widget;
	
	private void setjopjop_cseenabled_widget(BooleanOptionWidget widget) {
		jopjop_cseenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_cseenabled_widget() {
		return jopjop_cseenabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_csenaive_side_effect_widget;
	
	private void setjopjop_csenaive_side_effect_widget(BooleanOptionWidget widget) {
		jopjop_csenaive_side_effect_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_csenaive_side_effect_widget() {
		return jopjop_csenaive_side_effect_widget;
	}	
	
	private BooleanOptionWidget jopjop_bcmenabled_widget;
	
	private void setjopjop_bcmenabled_widget(BooleanOptionWidget widget) {
		jopjop_bcmenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_bcmenabled_widget() {
		return jopjop_bcmenabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_bcmnaive_side_effect_widget;
	
	private void setjopjop_bcmnaive_side_effect_widget(BooleanOptionWidget widget) {
		jopjop_bcmnaive_side_effect_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_bcmnaive_side_effect_widget() {
		return jopjop_bcmnaive_side_effect_widget;
	}	
	
	private BooleanOptionWidget jopjop_lcmenabled_widget;
	
	private void setjopjop_lcmenabled_widget(BooleanOptionWidget widget) {
		jopjop_lcmenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_lcmenabled_widget() {
		return jopjop_lcmenabled_widget;
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
	
	
	private MultiOptionWidget jopjop_lcmsafety_widget;
	
	private void setjopjop_lcmsafety_widget(MultiOptionWidget widget) {
		jopjop_lcmsafety_widget = widget;
	}
	
	public MultiOptionWidget getjopjop_lcmsafety_widget() {
		return jopjop_lcmsafety_widget;
	}	
	
	
	private BooleanOptionWidget jopjop_cpenabled_widget;
	
	private void setjopjop_cpenabled_widget(BooleanOptionWidget widget) {
		jopjop_cpenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_cpenabled_widget() {
		return jopjop_cpenabled_widget;
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
	
	private BooleanOptionWidget jopjop_cpfenabled_widget;
	
	private void setjopjop_cpfenabled_widget(BooleanOptionWidget widget) {
		jopjop_cpfenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_cpfenabled_widget() {
		return jopjop_cpfenabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_cbfenabled_widget;
	
	private void setjopjop_cbfenabled_widget(BooleanOptionWidget widget) {
		jopjop_cbfenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_cbfenabled_widget() {
		return jopjop_cbfenabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_daeenabled_widget;
	
	private void setjopjop_daeenabled_widget(BooleanOptionWidget widget) {
		jopjop_daeenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_daeenabled_widget() {
		return jopjop_daeenabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_daeonly_stack_locals_widget;
	
	private void setjopjop_daeonly_stack_locals_widget(BooleanOptionWidget widget) {
		jopjop_daeonly_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_daeonly_stack_locals_widget() {
		return jopjop_daeonly_stack_locals_widget;
	}	
	
	private BooleanOptionWidget jopjop_uce1enabled_widget;
	
	private void setjopjop_uce1enabled_widget(BooleanOptionWidget widget) {
		jopjop_uce1enabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_uce1enabled_widget() {
		return jopjop_uce1enabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_ubf1enabled_widget;
	
	private void setjopjop_ubf1enabled_widget(BooleanOptionWidget widget) {
		jopjop_ubf1enabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_ubf1enabled_widget() {
		return jopjop_ubf1enabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_uce2enabled_widget;
	
	private void setjopjop_uce2enabled_widget(BooleanOptionWidget widget) {
		jopjop_uce2enabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_uce2enabled_widget() {
		return jopjop_uce2enabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_ubf2enabled_widget;
	
	private void setjopjop_ubf2enabled_widget(BooleanOptionWidget widget) {
		jopjop_ubf2enabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_ubf2enabled_widget() {
		return jopjop_ubf2enabled_widget;
	}	
	
	private BooleanOptionWidget jopjop_uleenabled_widget;
	
	private void setjopjop_uleenabled_widget(BooleanOptionWidget widget) {
		jopjop_uleenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjopjop_uleenabled_widget() {
		return jopjop_uleenabled_widget;
	}	
	
	private BooleanOptionWidget japenabled_widget;
	
	private void setjapenabled_widget(BooleanOptionWidget widget) {
		japenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapenabled_widget() {
		return japenabled_widget;
	}	
	
	private BooleanOptionWidget japjap_npcenabled_widget;
	
	private void setjapjap_npcenabled_widget(BooleanOptionWidget widget) {
		japjap_npcenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_npcenabled_widget() {
		return japjap_npcenabled_widget;
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
	
	private BooleanOptionWidget japjap_abcenabled_widget;
	
	private void setjapjap_abcenabled_widget(BooleanOptionWidget widget) {
		japjap_abcenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_abcenabled_widget() {
		return japjap_abcenabled_widget;
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
	
	private BooleanOptionWidget japjap_profilingenabled_widget;
	
	private void setjapjap_profilingenabled_widget(BooleanOptionWidget widget) {
		japjap_profilingenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_profilingenabled_widget() {
		return japjap_profilingenabled_widget;
	}	
	
	private BooleanOptionWidget japjap_profilingnotmainentry_widget;
	
	private void setjapjap_profilingnotmainentry_widget(BooleanOptionWidget widget) {
		japjap_profilingnotmainentry_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_profilingnotmainentry_widget() {
		return japjap_profilingnotmainentry_widget;
	}	
	
	private BooleanOptionWidget japjap_seaenabled_widget;
	
	private void setjapjap_seaenabled_widget(BooleanOptionWidget widget) {
		japjap_seaenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_seaenabled_widget() {
		return japjap_seaenabled_widget;
	}	
	
	private BooleanOptionWidget japjap_seanaive_widget;
	
	private void setjapjap_seanaive_widget(BooleanOptionWidget widget) {
		japjap_seanaive_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_seanaive_widget() {
		return japjap_seanaive_widget;
	}	
	
	private BooleanOptionWidget japjap_fieldrwenabled_widget;
	
	private void setjapjap_fieldrwenabled_widget(BooleanOptionWidget widget) {
		japjap_fieldrwenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_fieldrwenabled_widget() {
		return japjap_fieldrwenabled_widget;
	}	
	
	
	private StringOptionWidget japjap_fieldrwthreshold_widget;
	
	private void setjapjap_fieldrwthreshold_widget(StringOptionWidget widget) {
		japjap_fieldrwthreshold_widget = widget;
	}
	
	public StringOptionWidget getjapjap_fieldrwthreshold_widget() {
		return japjap_fieldrwthreshold_widget;
	}
	
	
	private BooleanOptionWidget japjap_cgtaggerenabled_widget;
	
	private void setjapjap_cgtaggerenabled_widget(BooleanOptionWidget widget) {
		japjap_cgtaggerenabled_widget = widget;
	}
	
	public BooleanOptionWidget getjapjap_cgtaggerenabled_widget() {
		return japjap_cgtaggerenabled_widget;
	}	
	
	private BooleanOptionWidget gbenabled_widget;
	
	private void setgbenabled_widget(BooleanOptionWidget widget) {
		gbenabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbenabled_widget() {
		return gbenabled_widget;
	}	
	
	private BooleanOptionWidget gbgb_a1enabled_widget;
	
	private void setgbgb_a1enabled_widget(BooleanOptionWidget widget) {
		gbgb_a1enabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_a1enabled_widget() {
		return gbgb_a1enabled_widget;
	}	
	
	private BooleanOptionWidget gbgb_a1only_stack_locals_widget;
	
	private void setgbgb_a1only_stack_locals_widget(BooleanOptionWidget widget) {
		gbgb_a1only_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_a1only_stack_locals_widget() {
		return gbgb_a1only_stack_locals_widget;
	}	
	
	private BooleanOptionWidget gbgb_cfenabled_widget;
	
	private void setgbgb_cfenabled_widget(BooleanOptionWidget widget) {
		gbgb_cfenabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_cfenabled_widget() {
		return gbgb_cfenabled_widget;
	}	
	
	private BooleanOptionWidget gbgb_a2enabled_widget;
	
	private void setgbgb_a2enabled_widget(BooleanOptionWidget widget) {
		gbgb_a2enabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_a2enabled_widget() {
		return gbgb_a2enabled_widget;
	}	
	
	private BooleanOptionWidget gbgb_a2only_stack_locals_widget;
	
	private void setgbgb_a2only_stack_locals_widget(BooleanOptionWidget widget) {
		gbgb_a2only_stack_locals_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_a2only_stack_locals_widget() {
		return gbgb_a2only_stack_locals_widget;
	}	
	
	private BooleanOptionWidget gbgb_uleenabled_widget;
	
	private void setgbgb_uleenabled_widget(BooleanOptionWidget widget) {
		gbgb_uleenabled_widget = widget;
	}
	
	public BooleanOptionWidget getgbgb_uleenabled_widget() {
		return gbgb_uleenabled_widget;
	}	
	
	private BooleanOptionWidget gopenabled_widget;
	
	private void setgopenabled_widget(BooleanOptionWidget widget) {
		gopenabled_widget = widget;
	}
	
	public BooleanOptionWidget getgopenabled_widget() {
		return gopenabled_widget;
	}	
	
	private BooleanOptionWidget bbenabled_widget;
	
	private void setbbenabled_widget(BooleanOptionWidget widget) {
		bbenabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbenabled_widget() {
		return bbenabled_widget;
	}	
	
	private BooleanOptionWidget bbbb_lsoenabled_widget;
	
	private void setbbbb_lsoenabled_widget(BooleanOptionWidget widget) {
		bbbb_lsoenabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lsoenabled_widget() {
		return bbbb_lsoenabled_widget;
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
	
	private BooleanOptionWidget bbbb_phoenabled_widget;
	
	private void setbbbb_phoenabled_widget(BooleanOptionWidget widget) {
		bbbb_phoenabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_phoenabled_widget() {
		return bbbb_phoenabled_widget;
	}	
	
	private BooleanOptionWidget bbbb_uleenabled_widget;
	
	private void setbbbb_uleenabled_widget(BooleanOptionWidget widget) {
		bbbb_uleenabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_uleenabled_widget() {
		return bbbb_uleenabled_widget;
	}	
	
	private BooleanOptionWidget bbbb_lpenabled_widget;
	
	private void setbbbb_lpenabled_widget(BooleanOptionWidget widget) {
		bbbb_lpenabled_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lpenabled_widget() {
		return bbbb_lpenabled_widget;
	}	
	
	private BooleanOptionWidget bbbb_lpunsplit_original_locals_widget;
	
	private void setbbbb_lpunsplit_original_locals_widget(BooleanOptionWidget widget) {
		bbbb_lpunsplit_original_locals_widget = widget;
	}
	
	public BooleanOptionWidget getbbbb_lpunsplit_original_locals_widget() {
		return bbbb_lpunsplit_original_locals_widget;
	}	
	
	private BooleanOptionWidget bopenabled_widget;
	
	private void setbopenabled_widget(BooleanOptionWidget widget) {
		bopenabled_widget = widget;
	}
	
	public BooleanOptionWidget getbopenabled_widget() {
		return bopenabled_widget;
	}	
	
	private BooleanOptionWidget tagenabled_widget;
	
	private void settagenabled_widget(BooleanOptionWidget widget) {
		tagenabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagenabled_widget() {
		return tagenabled_widget;
	}	
	
	private BooleanOptionWidget tagtag_lnenabled_widget;
	
	private void settagtag_lnenabled_widget(BooleanOptionWidget widget) {
		tagtag_lnenabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagtag_lnenabled_widget() {
		return tagtag_lnenabled_widget;
	}	
	
	private BooleanOptionWidget tagtag_anenabled_widget;
	
	private void settagtag_anenabled_widget(BooleanOptionWidget widget) {
		tagtag_anenabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagtag_anenabled_widget() {
		return tagtag_anenabled_widget;
	}	
	
	private BooleanOptionWidget tagtag_depenabled_widget;
	
	private void settagtag_depenabled_widget(BooleanOptionWidget widget) {
		tagtag_depenabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagtag_depenabled_widget() {
		return tagtag_depenabled_widget;
	}	
	
	private BooleanOptionWidget tagtag_fieldrwenabled_widget;
	
	private void settagtag_fieldrwenabled_widget(BooleanOptionWidget widget) {
		tagtag_fieldrwenabled_widget = widget;
	}
	
	public BooleanOptionWidget gettagtag_fieldrwenabled_widget() {
		return tagtag_fieldrwenabled_widget;
	}	
	

	private ListOptionWidget Single_File_Mode_Optionsprocess_path_widget;
	
	private void setSingle_File_Mode_Optionsprocess_path_widget(ListOptionWidget widget) {
		Single_File_Mode_Optionsprocess_path_widget = widget;
	}
	
	public ListOptionWidget getSingle_File_Mode_Optionsprocess_path_widget() {
		return Single_File_Mode_Optionsprocess_path_widget;
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
		
		Group editGroupGeneral_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupGeneral_Options.setLayout(layout);
	
	 	editGroupGeneral_Options.setText("General Options");
	 	
		editGroupGeneral_Options.setData("id", "General_Options");
		
		String descGeneral_Options = "";	
		if (descGeneral_Options.length() > 0) {
			Label descLabelGeneral_Options = new Label(editGroupGeneral_Options, SWT.WRAP);
			descLabelGeneral_Options.setText(descGeneral_Options);
		}
		OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"h";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionshelp_widget(new BooleanOptionWidget(editGroupGeneral_Options, SWT.NONE, new OptionData("Help", "", "","h", "\nThis option displays the textual help message and exits \nimmediately without doing any further processing.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"pl";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionsphase_list_widget(new BooleanOptionWidget(editGroupGeneral_Options, SWT.NONE, new OptionData("Phase List", "", "","pl", "\nThis option causes Soot to print a list of the available phases \nand sub-phases, and exit.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"version";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionsversion_widget(new BooleanOptionWidget(editGroupGeneral_Options, SWT.NONE, new OptionData("Version", "", "","version", "\nThis option displays the Soot version information and exits \nimmediately without doing any further processing.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"v";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionsverbose_widget(new BooleanOptionWidget(editGroupGeneral_Options, SWT.NONE, new OptionData("Verbose", "", "","v", "\nThis option causes Soot to display detailed information about \nwhat it is doing.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"app";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionsapp_widget(new BooleanOptionWidget(editGroupGeneral_Options, SWT.NONE, new OptionData("Application Mode", "", "","app", "\nThis option causes Soot to process any application classes \nreferenced by the classes specified on the command line, in \naddition to the specified classes.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"w";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionswhole_program_widget(new BooleanOptionWidget(editGroupGeneral_Options, SWT.NONE, new OptionData("Whole-Program Mode", "", "","w", "\nThis option causes Soot to run in whole program mode, taking \ninto consideration the whole program when performing \noptimizations.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"debug";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setGeneral_Optionsdebug_widget(new BooleanOptionWidget(editGroupGeneral_Options, SWT.NONE, new OptionData("Debug", "", "","debug", "\nThis option causes Soot to print various debugging information, \nparticularly from the Baf Body Phase and the Jimple Annotation \nPack Phase.", defaultBool)));
		
		

		defKey = ""+" "+""+" "+"ph";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setGeneral_Optionsphase_help_widget(new ListOptionWidget(editGroupGeneral_Options, SWT.NONE, new OptionData("Phase Help",  "", "","ph", "\nThis option causes Soot to print a help message about the phase \nor sub-phase specified by its argument, and exit. \n", defaultString)));
		

		
		return editGroupGeneral_Options;
	}



	private Composite Input_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupInput_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupInput_Options.setLayout(layout);
	
	 	editGroupInput_Options.setText("Input Options");
	 	
		editGroupInput_Options.setData("id", "Input_Options");
		
		String descInput_Options = "";	
		if (descInput_Options.length() > 0) {
			Label descLabelInput_Options = new Label(editGroupInput_Options, SWT.WRAP);
			descLabelInput_Options.setText(descInput_Options);
		}
		OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"allow-phantom-refs";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setInput_Optionsallow_phantom_refs_widget(new BooleanOptionWidget(editGroupInput_Options, SWT.NONE, new OptionData("Allow Phantom References", "", "","allow-phantom-refs", "\nThis option causes Soot to process a class even if it cannot \nfind classes referenced by that class. This may cause Soot to \nproduce incorrect results.", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Class File",
		"c",
		"\n",
		
		true),
		
		new OptionData("Jimple File",
		"J",
		"\n",
		
		false),
		
		};
		
										
		setInput_Optionssrc_prec_widget(new MultiOptionWidget(editGroupInput_Options, SWT.NONE, data, new OptionData("Input Source Precedence", "", "","src-prec", "\nBy default, Soot will resolve classes from .class files. If a \nclass cannot be resolved from a .class file, Soot will attempt \nto resolve it from a .jimple file. Setting this option to jimple \nspecifies the opposite policy: classes are resolved from .jimple \nfiles, and only if this fails will an attempt be made to resolve \nthem from .class files.")));
		
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

		setInput_Optionssoot_classpath_widget(new StringOptionWidget(editGroupInput_Options, SWT.NONE, new OptionData("Soot Classpath",  "", "","cp", "\nUses given PATH as the classpath for finding classes for Soot \nprocessing.", defaultString)));
		

		
		return editGroupInput_Options;
	}



	private Composite Output_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupOutput_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupOutput_Options.setLayout(layout);
	
	 	editGroupOutput_Options.setText("Output Options");
	 	
		editGroupOutput_Options.setData("id", "Output_Options");
		
		String descOutput_Options = "";	
		if (descOutput_Options.length() > 0) {
			Label descLabelOutput_Options = new Label(editGroupOutput_Options, SWT.WRAP);
			descLabelOutput_Options.setText(descOutput_Options);
		}
		OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"via-grimp";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setOutput_Optionsvia_grimp_widget(new BooleanOptionWidget(editGroupOutput_Options, SWT.NONE, new OptionData("Via Grimp", "", "","via-grimp", "\nThis option converts Jimple to bytecode via Grimp instead of via \nBaf.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"xml-attributes";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setOutput_Optionsxml_attributes_widget(new BooleanOptionWidget(editGroupOutput_Options, SWT.NONE, new OptionData("Save Tags to XML", "", "","xml-attributes", "\nThis option saves a variety of tags which are attached to hosts \nto an XML file that is later read by Eclipse to show program \nunderstanding annotations within Eclipse.", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Jimple File",
		"J",
		"\nProduces .jimple files that contain a textual representation for \ntypes in Soot's Jimple internal representation.",
		
		false),
		
		new OptionData("Jimp File",
		"j",
		"\nProduces .jimp files which contain an abbreviated \ntextual form for Soot's Jimple internal representation. The \nabbreviated form is easier to read than the non-abbreviated \ncounterpart, but may also contain ambiguities; for \ninstance, method signatures are not uniquely determined.",
		
		false),
		
		new OptionData("Shimple File",
		"S",
		"\nProduces .shimple files containing the textual \nrepresentation of Soot's SSA Shimple internal \nrepresentation. Shimple is very similar to Jimple except \nthat it supports Phi nodes.",
		
		false),
		
		new OptionData("Shimp File",
		"s",
		"\nProduces .shimp files which contain an abbreviated \ntextual form for Soot's SSA Shimple internal representation. \nThe abbreviated form is easier to read than the \nnon-abbreviated counterpart, but may also contain ambiguities; \nfor instance, method signatures are not uniquely \ndetermined.",
		
		false),
		
		new OptionData("Baf File",
		"B",
		"\nProduces .baf files that contain a textual representation for \ntypes in Soot's Baf internal representation.",
		
		false),
		
		new OptionData("Abbreviated Baf File",
		"b",
		"\nProduces .b files. These contain an abbreviated textual form for \nSoot's Baf internal representation. It is easier to read than \nits non-abbreviated counterpart, but can also contain \nambiguities; for instance, method signatures are not uniquely \ndetermined.",
		
		false),
		
		new OptionData("Grimple File",
		"G",
		"\nProduces .grimple files that contain a textual representation \nfor types in Soot's Grimp internal representation.",
		
		false),
		
		new OptionData("Grimp File",
		"g",
		"\nProduces .grimp files which contain an abbreviated \ntextual form for Soot's Grimple internal representation. \nThe abbreviated form is easier to read than the \nnon-abbreviated counterpart, but may also contain ambiguities; \nfor instance, method signatures are not uniquely \ndetermined.",
		
		false),
		
		new OptionData("Xml File",
		"X",
		"\nProduces .xml files of classes based on the Jimple statements.",
		
		false),
		
		new OptionData("No Output File",
		"n",
		"\nThis option causes Soot to produces no output files.",
		
		false),
		
		new OptionData("Jasmin File",
		"jasmin",
		"\nProduces .jasmin files as output. These can be understood by the \njasmin bytecode assembler tool.",
		
		false),
		
		new OptionData("Class File",
		"c",
		"\nProduces Java .class files executable under any Java Virtual \nMachine.",
		
		true),
		
		new OptionData("Dava Decompiled File",
		"d",
		"\nProduces dava decompiled .java files.",
		
		false),
		
		};
		
										
		setOutput_Optionsoutput_format_widget(new MultiOptionWidget(editGroupOutput_Options, SWT.NONE, data, new OptionData("Output Format", "", "","f", "\nThis option sets the output format of files Soot will produce or \nno output.")));
		
		defKey = ""+" "+""+" "+"f";
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

		setOutput_Optionsoutput_dir_widget(new StringOptionWidget(editGroupOutput_Options, SWT.NONE, new OptionData("Output Directory",  "", "","d", "\nSpecifies that the outputted files are to be stored in PATH. The \npath may be relative to the working directory. This PATH must \nalready exist before running Soot.", defaultString)));
		

		
		return editGroupOutput_Options;
	}



	private Composite Processing_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupProcessing_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupProcessing_Options.setLayout(layout);
	
	 	editGroupProcessing_Options.setText("Processing Options");
	 	
		editGroupProcessing_Options.setData("id", "Processing_Options");
		
		String descProcessing_Options = "";	
		if (descProcessing_Options.length() > 0) {
			Label descLabelProcessing_Options = new Label(editGroupProcessing_Options, SWT.WRAP);
			descLabelProcessing_Options.setText(descProcessing_Options);
		}
		OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"O";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setProcessing_Optionsoptimize_widget(new BooleanOptionWidget(editGroupProcessing_Options, SWT.NONE, new OptionData("Optimize", "", "","O", "\nPerform scalar optimizations on the classfiles.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"W";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setProcessing_Optionswhole_optimize_widget(new BooleanOptionWidget(editGroupProcessing_Options, SWT.NONE, new OptionData("Whole Program Optimize", "", "","W", "\nPerform whole program optimizations on the classfiles; this also \nenables -O.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"via-shimple";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setProcessing_Optionsvia_shimple_widget(new BooleanOptionWidget(editGroupProcessing_Options, SWT.NONE, new OptionData("Via Shimple", "", "","via-shimple", "\nThis option ... ", defaultBool)));
		
		

		
		return editGroupProcessing_Options;
	}



	private Composite jbCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjb = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjb.setLayout(layout);
	
	 	editGroupjb.setText("Jimple Body Creation");
	 	
		editGroupjb.setData("id", "jb");
		
		String descjb = "Create a JimpleBody for each method";	
		if (descjb.length() > 0) {
			Label descLabeljb = new Label(editGroupjb, SWT.WRAP);
			descLabeljb.setText(descjb);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbenabled_widget(new BooleanOptionWidget(editGroupjb, SWT.NONE, new OptionData("Enabled", "p", "jb","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb"+" "+"use-original-names";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbuse_original_names_widget(new BooleanOptionWidget(editGroupjb, SWT.NONE, new OptionData("Use Original Names", "p", "jb","use-original-names", "\nRetain the original names for local variables when the source \nincludes those names. Otherwise, Soot gives variables generic \nnames based on their types. ", defaultBool)));
		
		

		
		return editGroupjb;
	}



	private Composite jbjb_lsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_ls = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_ls.setLayout(layout);
	
	 	editGroupjbjb_ls.setText("Local Splitter");
	 	
		editGroupjbjb_ls.setData("id", "jbjb_ls");
		
		String descjbjb_ls = "Associates separate locals with each use-def web";	
		if (descjbjb_ls.length() > 0) {
			Label descLabeljbjb_ls = new Label(editGroupjbjb_ls, SWT.WRAP);
			descLabeljbjb_ls.setText(descjbjb_ls);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.ls"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_lsenabled_widget(new BooleanOptionWidget(editGroupjbjb_ls, SWT.NONE, new OptionData("Enabled", "p", "jb.ls","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjbjb_ls;
	}



	private Composite jbjb_aCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_a = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_a.setLayout(layout);
	
	 	editGroupjbjb_a.setText("Jimple Local Aggregator");
	 	
		editGroupjbjb_a.setData("id", "jbjb_a");
		
		String descjbjb_a = "Removes some unnecessary copies";	
		if (descjbjb_a.length() > 0) {
			Label descLabeljbjb_a = new Label(editGroupjbjb_a, SWT.WRAP);
			descLabeljbjb_a.setText(descjbjb_a);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.a"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_aenabled_widget(new BooleanOptionWidget(editGroupjbjb_a, SWT.NONE, new OptionData("Enabled", "p", "jb.a","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.a"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_aonly_stack_locals_widget(new BooleanOptionWidget(editGroupjbjb_a, SWT.NONE, new OptionData("Only Stack Locals", "p", "jb.a","only-stack-locals", "\nOnly aggregate locals that represent stack locations in the \noriginal bytecode. (Stack locals can be distinguished in Jimple \nby the character with which their names begin.) ", defaultBool)));
		
		

		
		return editGroupjbjb_a;
	}



	private Composite jbjb_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_ule = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_ule.setLayout(layout);
	
	 	editGroupjbjb_ule.setText("Unused Local Eliminator");
	 	
		editGroupjbjb_ule.setData("id", "jbjb_ule");
		
		String descjbjb_ule = "Removes unused locals";	
		if (descjbjb_ule.length() > 0) {
			Label descLabeljbjb_ule = new Label(editGroupjbjb_ule, SWT.WRAP);
			descLabeljbjb_ule.setText(descjbjb_ule);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.ule"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_uleenabled_widget(new BooleanOptionWidget(editGroupjbjb_ule, SWT.NONE, new OptionData("Enabled", "p", "jb.ule","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjbjb_ule;
	}



	private Composite jbjb_trCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_tr = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_tr.setLayout(layout);
	
	 	editGroupjbjb_tr.setText("Type Assigner");
	 	
		editGroupjbjb_tr.setData("id", "jbjb_tr");
		
		String descjbjb_tr = "Assigns types to locals";	
		if (descjbjb_tr.length() > 0) {
			Label descLabeljbjb_tr = new Label(editGroupjbjb_tr, SWT.WRAP);
			descLabeljbjb_tr.setText(descjbjb_tr);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.tr"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_trenabled_widget(new BooleanOptionWidget(editGroupjbjb_tr, SWT.NONE, new OptionData("Enabled", "p", "jb.tr","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjbjb_tr;
	}



	private Composite jbjb_ulpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_ulp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_ulp.setLayout(layout);
	
	 	editGroupjbjb_ulp.setText("Unsplit-originals Local Packer");
	 	
		editGroupjbjb_ulp.setData("id", "jbjb_ulp");
		
		String descjbjb_ulp = "Minimizes number of locals";	
		if (descjbjb_ulp.length() > 0) {
			Label descLabeljbjb_ulp = new Label(editGroupjbjb_ulp, SWT.WRAP);
			descLabeljbjb_ulp.setText(descjbjb_ulp);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.ulp"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_ulpenabled_widget(new BooleanOptionWidget(editGroupjbjb_ulp, SWT.NONE, new OptionData("Enabled", "p", "jb.ulp","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.ulp"+" "+"unsplit-original-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_ulpunsplit_original_locals_widget(new BooleanOptionWidget(editGroupjbjb_ulp, SWT.NONE, new OptionData("Unsplit Original Locals", "p", "jb.ulp","unsplit-original-locals", "\nUse the variable names in the original source as a guide when \ndetermining how to share local variables among non-interfering \nvariable usages. This recombines named locals which were split \nby the Local Splitter. ", defaultBool)));
		
		

		
		return editGroupjbjb_ulp;
	}



	private Composite jbjb_lnsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_lns = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_lns.setLayout(layout);
	
	 	editGroupjbjb_lns.setText("Local Name Standardizer");
	 	
		editGroupjbjb_lns.setData("id", "jbjb_lns");
		
		String descjbjb_lns = "Gives names to locals";	
		if (descjbjb_lns.length() > 0) {
			Label descLabeljbjb_lns = new Label(editGroupjbjb_lns, SWT.WRAP);
			descLabeljbjb_lns.setText(descjbjb_lns);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.lns"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_lnsenabled_widget(new BooleanOptionWidget(editGroupjbjb_lns, SWT.NONE, new OptionData("Enabled", "p", "jb.lns","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.lns"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_lnsonly_stack_locals_widget(new BooleanOptionWidget(editGroupjbjb_lns, SWT.NONE, new OptionData("Only Stack Locals", "p", "jb.lns","only-stack-locals", "\nOnly standardizes the names of variables that represent stack \nlocations in the original bytecode. This becomes the default \nwhen the `use-original-names' option is specified for the `jb' \nphase. ", defaultBool)));
		
		

		
		return editGroupjbjb_lns;
	}



	private Composite jbjb_cpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_cp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_cp.setLayout(layout);
	
	 	editGroupjbjb_cp.setText("Copy Propagator");
	 	
		editGroupjbjb_cp.setData("id", "jbjb_cp");
		
		String descjbjb_cp = "Removes unnecessary copies";	
		if (descjbjb_cp.length() > 0) {
			Label descLabeljbjb_cp = new Label(editGroupjbjb_cp, SWT.WRAP);
			descLabeljbjb_cp.setText(descjbjb_cp);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.cp"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_cpenabled_widget(new BooleanOptionWidget(editGroupjbjb_cp, SWT.NONE, new OptionData("Enabled", "p", "jb.cp","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.cp"+" "+"only-regular-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_cponly_regular_locals_widget(new BooleanOptionWidget(editGroupjbjb_cp, SWT.NONE, new OptionData("Only Regular Locals", "p", "jb.cp","only-regular-locals", "\nOnly propagate copies through ``regular'' locals, that is, \nthose declared in the source bytecode. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.cp"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_cponly_stack_locals_widget(new BooleanOptionWidget(editGroupjbjb_cp, SWT.NONE, new OptionData("Only Stack Locals", "p", "jb.cp","only-stack-locals", "\nOnly propagate copies through locals that represent stack \nlocations in the original bytecode. ", defaultBool)));
		
		

		
		return editGroupjbjb_cp;
	}



	private Composite jbjb_daeCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_dae = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_dae.setLayout(layout);
	
	 	editGroupjbjb_dae.setText("Dead Assignment Eliminator");
	 	
		editGroupjbjb_dae.setData("id", "jbjb_dae");
		
		String descjbjb_dae = "";	
		if (descjbjb_dae.length() > 0) {
			Label descLabeljbjb_dae = new Label(editGroupjbjb_dae, SWT.WRAP);
			descLabeljbjb_dae.setText(descjbjb_dae);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.dae"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_daeenabled_widget(new BooleanOptionWidget(editGroupjbjb_dae, SWT.NONE, new OptionData("Enabled", "p", "jb.dae","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.dae"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_daeonly_stack_locals_widget(new BooleanOptionWidget(editGroupjbjb_dae, SWT.NONE, new OptionData("Only Stack Locals", "p", "jb.dae","only-stack-locals", "\nOnly eliminate dead assignments to locals that represent stack \nlocations in the original bytecode. ", defaultBool)));
		
		

		
		return editGroupjbjb_dae;
	}



	private Composite jbjb_cp_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_cp_ule = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_cp_ule.setLayout(layout);
	
	 	editGroupjbjb_cp_ule.setText("Post-copy propagation Unused Local Eliminator");
	 	
		editGroupjbjb_cp_ule.setData("id", "jbjb_cp_ule");
		
		String descjbjb_cp_ule = "Removes unused locals";	
		if (descjbjb_cp_ule.length() > 0) {
			Label descLabeljbjb_cp_ule = new Label(editGroupjbjb_cp_ule, SWT.WRAP);
			descLabeljbjb_cp_ule.setText(descjbjb_cp_ule);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.cp-ule"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_cp_uleenabled_widget(new BooleanOptionWidget(editGroupjbjb_cp_ule, SWT.NONE, new OptionData("Enabled", "p", "jb.cp-ule","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjbjb_cp_ule;
	}



	private Composite jbjb_lpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_lp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_lp.setLayout(layout);
	
	 	editGroupjbjb_lp.setText("Local Packer");
	 	
		editGroupjbjb_lp.setData("id", "jbjb_lp");
		
		String descjbjb_lp = "Minimizes number of locals";	
		if (descjbjb_lp.length() > 0) {
			Label descLabeljbjb_lp = new Label(editGroupjbjb_lp, SWT.WRAP);
			descLabeljbjb_lp.setText(descjbjb_lp);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.lp"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_lpenabled_widget(new BooleanOptionWidget(editGroupjbjb_lp, SWT.NONE, new OptionData("Enabled", "p", "jb.lp","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jb.lp"+" "+"unsplit-original-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjbjb_lpunsplit_original_locals_widget(new BooleanOptionWidget(editGroupjbjb_lp, SWT.NONE, new OptionData("Unsplit Original Locals", "p", "jb.lp","unsplit-original-locals", "\nUse the variable names in the original source as a guide when \ndetermining how to share local variables across non-interfering \nvariable usages. This recombines named locals which were split \nby the Local Splitter. SHOULD WE ENSURE THAT IF jb.ulp IS ALSO \nENABLED, THEN ITS unsplit-original-locals MATCHES THIS ONE? ", defaultBool)));
		
		

		
		return editGroupjbjb_lp;
	}



	private Composite jbjb_neCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_ne = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_ne.setLayout(layout);
	
	 	editGroupjbjb_ne.setText("Nop Eliminator");
	 	
		editGroupjbjb_ne.setData("id", "jbjb_ne");
		
		String descjbjb_ne = "";	
		if (descjbjb_ne.length() > 0) {
			Label descLabeljbjb_ne = new Label(editGroupjbjb_ne, SWT.WRAP);
			descLabeljbjb_ne.setText(descjbjb_ne);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.ne"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_neenabled_widget(new BooleanOptionWidget(editGroupjbjb_ne, SWT.NONE, new OptionData("Enabled", "p", "jb.ne","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjbjb_ne;
	}



	private Composite jbjb_uceCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjbjb_uce = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjbjb_uce.setLayout(layout);
	
	 	editGroupjbjb_uce.setText("Unreachable Code Eliminator");
	 	
		editGroupjbjb_uce.setData("id", "jbjb_uce");
		
		String descjbjb_uce = "";	
		if (descjbjb_uce.length() > 0) {
			Label descLabeljbjb_uce = new Label(editGroupjbjb_uce, SWT.WRAP);
			descLabeljbjb_uce.setText(descjbjb_uce);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jb.uce"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjbjb_uceenabled_widget(new BooleanOptionWidget(editGroupjbjb_uce, SWT.NONE, new OptionData("Enabled", "p", "jb.uce","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjbjb_uce;
	}



	private Composite cgCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupcg = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupcg.setLayout(layout);
	
	 	editGroupcg.setText("Call Graph");
	 	
		editGroupcg.setData("id", "cg");
		
		String desccg = "Build a call graph";	
		if (desccg.length() > 0) {
			Label descLabelcg = new Label(editGroupcg, SWT.WRAP);
			descLabelcg.setText(desccg);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"cg"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgenabled_widget(new BooleanOptionWidget(editGroupcg, SWT.NONE, new OptionData("Enabled", "p", "cg","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg"+" "+"safe-forname";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgsafe_forname_widget(new BooleanOptionWidget(editGroupcg, SWT.NONE, new OptionData("Safe forName", "p", "cg","safe-forname", "\nWhen a program calls Class.forName(), the named class is \nresolved, and its static initializer executed. In many cases, it \ncannot be determined statically which class will be loaded, and \nwhich static initializer executed. When this option is set to \ntrue, Soot will conservatively assume that any static \ninitializer could be executed. This may make the call graph very \nlarge. When this option is set to false, any calls to \nClass.forName() for which the class cannot be determined \nstatically are not assumed to call any static initializers. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg"+" "+"verbose";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgverbose_widget(new BooleanOptionWidget(editGroupcg, SWT.NONE, new OptionData("Verbose", "p", "cg","verbose", "\nDue to the effects of native methods and reflection, it may not \nalways be possible to construct a fully conservative call graph. \nSetting this option to true causes Soot to point out the parts \nof the call graph that may be incomplete, so that they can be \nchecked by hand. ", defaultBool)));
		
		

		
		return editGroupcg;
	}



	private Composite cgcg_chaCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupcgcg_cha = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupcgcg_cha.setLayout(layout);
	
	 	editGroupcgcg_cha.setText("Class Hierarchy Analysis");
	 	
		editGroupcgcg_cha.setData("id", "cgcg_cha");
		
		String desccgcg_cha = "Build a call graph using Class Hierarchy Analysis";	
		if (desccgcg_cha.length() > 0) {
			Label descLabelcgcg_cha = new Label(editGroupcgcg_cha, SWT.WRAP);
			descLabelcgcg_cha.setText(desccgcg_cha);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.cha"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_chaenabled_widget(new BooleanOptionWidget(editGroupcgcg_cha, SWT.NONE, new OptionData("Enabled", "p", "cg.cha","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.cha"+" "+"verbose";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_chaverbose_widget(new BooleanOptionWidget(editGroupcgcg_cha, SWT.NONE, new OptionData("Verbose", "p", "cg.cha","verbose", "\nSetting this option to true causes Soot to print out statistics \nabout the call graph computed by this phase, such as the number \nof methods determined to be reachable.", defaultBool)));
		
		

		
		return editGroupcgcg_cha;
	}



	private Composite cgcg_sparkCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupcgcg_spark = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupcgcg_spark.setLayout(layout);
	
	 	editGroupcgcg_spark.setText("Spark");
	 	
		editGroupcgcg_spark.setData("id", "cgcg_spark");
		
		String desccgcg_spark = "Spark points-to analysis framework";	
		if (desccgcg_spark.length() > 0) {
			Label descLabelcgcg_spark = new Label(editGroupcgcg_spark, SWT.WRAP);
			descLabelcgcg_spark.setText(desccgcg_spark);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkenabled_widget(new BooleanOptionWidget(editGroupcgcg_spark, SWT.NONE, new OptionData("Enabled", "p", "cg.spark","enabled", "\n", defaultBool)));
		
		

		
		return editGroupcgcg_spark;
	}



	private Composite cgSpark_General_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupcgSpark_General_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupcgSpark_General_Options.setLayout(layout);
	
	 	editGroupcgSpark_General_Options.setText("Spark General Options");
	 	
		editGroupcgSpark_General_Options.setData("id", "cgSpark_General_Options");
		
		String desccgSpark_General_Options = "";	
		if (desccgSpark_General_Options.length() > 0) {
			Label descLabelcgSpark_General_Options = new Label(editGroupcgSpark_General_Options, SWT.WRAP);
			descLabelcgSpark_General_Options.setText(desccgSpark_General_Options);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"verbose";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkverbose_widget(new BooleanOptionWidget(editGroupcgSpark_General_Options, SWT.NONE, new OptionData("Verbose", "p", "cg.spark","verbose", "\nWhen this option is set to true, Spark prints detailed \ninformation about its execution. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"ignore-types";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkignore_types_widget(new BooleanOptionWidget(editGroupcgSpark_General_Options, SWT.NONE, new OptionData("Ignore Types Entirely", "p", "cg.spark","ignore-types", "\nWhen this option is set to true, all parts of Spark completely \nignore declared types of variables and casts. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"force-gc";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkforce_gc_widget(new BooleanOptionWidget(editGroupcgSpark_General_Options, SWT.NONE, new OptionData("Force Garbage Collections", "p", "cg.spark","force-gc", "\nWhen this option is set to true, calls to System.gc() will be \nmade at various points to allow memory usage to be measured. \n", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"pre-jimplify";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkpre_jimplify_widget(new BooleanOptionWidget(editGroupcgSpark_General_Options, SWT.NONE, new OptionData("Pre Jimplify", "p", "cg.spark","pre-jimplify", "\nWhen this option is set to true, Spark converts all available \nmethods to Jimple before starting the points-to analysis. This \nallows the Jimplification time to be separated from the \npoints-to time. However, it increases the total time and memory \nrequirement, because all methods are Jimplified, rather than \nonly those deemed reachable by the points-to analysis. ", defaultBool)));
		
		

		
		return editGroupcgSpark_General_Options;
	}



	private Composite cgSpark_Pointer_Assignment_Graph_Building_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupcgSpark_Pointer_Assignment_Graph_Building_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupcgSpark_Pointer_Assignment_Graph_Building_Options.setLayout(layout);
	
	 	editGroupcgSpark_Pointer_Assignment_Graph_Building_Options.setText("Spark Pointer Assignment Graph Building Options");
	 	
		editGroupcgSpark_Pointer_Assignment_Graph_Building_Options.setData("id", "cgSpark_Pointer_Assignment_Graph_Building_Options");
		
		String desccgSpark_Pointer_Assignment_Graph_Building_Options = "";	
		if (desccgSpark_Pointer_Assignment_Graph_Building_Options.length() > 0) {
			Label descLabelcgSpark_Pointer_Assignment_Graph_Building_Options = new Label(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.WRAP);
			descLabelcgSpark_Pointer_Assignment_Graph_Building_Options.setText(desccgSpark_Pointer_Assignment_Graph_Building_Options);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"vta";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkvta_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("VTA", "p", "cg.spark","vta", "\nSetting VTA to true has the effect of setting field-based, \ntypes-for-sites, and simplify-sccs to true to simulate Variable \nType Analysis, described in our OOPSLA 2000 paper. Note that the \nalgorithm differs from the original VTA in that it handles array \nelements more precisely. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"rta";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkrta_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("RTA", "p", "cg.spark","rta", "\nSetting RTA to true sets types-for-sites to true, and causes \nSpark to use a single points-to set for all variables, giving \nRapid Type Analysis. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"field-based";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkfield_based_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("Field Based", "p", "cg.spark","field-based", "\nWhen this option is set to true, fields are represented by \nvariable (Green) nodes, and the object that the field belongs to \nis ignored (all objects are lumped together), giving a \nfield-based analysis. Otherwise, fields are represented by field \nreference (Red) nodes, and the objects that they belong to are \ndistinguished, giving a field-sensitive analysis. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"types-for-sites";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparktypes_for_sites_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("Types For Sites", "p", "cg.spark","types-for-sites", "\nWhen this option is set to true, types rather than allocation \nsites are used as the elements of the points-to sets. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"merge-stringbuffer";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparkmerge_stringbuffer_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("Merge String Buffer", "p", "cg.spark","merge-stringbuffer", "\nWhen this option is set to true, all allocation sites creating \njava.lang.StringBuffer objects are grouped together as a single \nallocation site. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"simulate-natives";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparksimulate_natives_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("Simulate Natives", "p", "cg.spark","simulate-natives", "\nWhen this option is set to true, the effects of native methods \nin the standard Java class library are simulated. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"simple-edges-bidirectional";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparksimple_edges_bidirectional_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("Simple Edges Bidirectional", "p", "cg.spark","simple-edges-bidirectional", "\nWhen this option is set to true, all edges connecting variable \n(Green) nodes are made bidirectional, as in Steensgaard's \nanalysis. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"on-fly-cg";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparkon_fly_cg_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("On Fly Call Graph", "p", "cg.spark","on-fly-cg", "\nWhen this option is set to true, the call graph is computed \non-the-fly as points-to information is computed. Otherwise, an \ninitial CHA approximation to the call graph is used. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"parms-as-fields";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkparms_as_fields_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("Parms As Fields", "p", "cg.spark","parms-as-fields", "\nWhen this option is set to true, parameters to methods are \nrepresented as fields (Red nodes) of the this object; otherwise, \nparameters are represented as variable (Green) nodes. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"returns-as-fields";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkreturns_as_fields_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Building_Options, SWT.NONE, new OptionData("Returns As Fields", "p", "cg.spark","returns-as-fields", "\nWhen this option is set to true, return values from methods are \nrepresented as fields (Red nodes) of the this object; otherwise, \nreturn values are represented as variable (Green) nodes. \n", defaultBool)));
		
		

		
		return editGroupcgSpark_Pointer_Assignment_Graph_Building_Options;
	}



	private Composite cgSpark_Pointer_Assignment_Graph_Simplification_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupcgSpark_Pointer_Assignment_Graph_Simplification_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupcgSpark_Pointer_Assignment_Graph_Simplification_Options.setLayout(layout);
	
	 	editGroupcgSpark_Pointer_Assignment_Graph_Simplification_Options.setText("Spark Pointer Assignment Graph Simplification Options");
	 	
		editGroupcgSpark_Pointer_Assignment_Graph_Simplification_Options.setData("id", "cgSpark_Pointer_Assignment_Graph_Simplification_Options");
		
		String desccgSpark_Pointer_Assignment_Graph_Simplification_Options = "";	
		if (desccgSpark_Pointer_Assignment_Graph_Simplification_Options.length() > 0) {
			Label descLabelcgSpark_Pointer_Assignment_Graph_Simplification_Options = new Label(editGroupcgSpark_Pointer_Assignment_Graph_Simplification_Options, SWT.WRAP);
			descLabelcgSpark_Pointer_Assignment_Graph_Simplification_Options.setText(desccgSpark_Pointer_Assignment_Graph_Simplification_Options);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"simplify-offline";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparksimplify_offline_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Simplification_Options, SWT.NONE, new OptionData("Simplify Offline", "p", "cg.spark","simplify-offline", "\nWhen this option is set to true, variable (Green) nodes which \nforming single-entry subgraphs (so they must have the same \npoints-to set) are merged together before propagation begins. \n", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"simplify-sccs";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparksimplify_sccs_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Simplification_Options, SWT.NONE, new OptionData("Simplify SCCs", "p", "cg.spark","simplify-sccs", "\nWhen this option is set to true, variable (Green) nodes which \nform strongly-connected components (so they must have the same \npoints-to set) are merged together before propagation begins. \n", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"ignore-types-for-sccs";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkignore_types_for_sccs_widget(new BooleanOptionWidget(editGroupcgSpark_Pointer_Assignment_Graph_Simplification_Options, SWT.NONE, new OptionData("Ignore Types For SCCs", "p", "cg.spark","ignore-types-for-sccs", "\nWhen this option is set to true, when collapsing \nstrongly-connected components, nodes forming SCCs are collapsed \nregardless of their declared type. The collapsed SCC is given \nthe most general type of all the nodes in the component. When \nthis option is set to false, only edges connecting nodes of the \nsame type are considered when detecting SCCs. This option has \nno effect unless simplify-sccs is true. ", defaultBool)));
		
		

		
		return editGroupcgSpark_Pointer_Assignment_Graph_Simplification_Options;
	}



	private Composite cgSpark_Points_To_Set_Flowing_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupcgSpark_Points_To_Set_Flowing_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupcgSpark_Points_To_Set_Flowing_Options.setLayout(layout);
	
	 	editGroupcgSpark_Points_To_Set_Flowing_Options.setText("Spark Points-To Set Flowing Options");
	 	
		editGroupcgSpark_Points_To_Set_Flowing_Options.setData("id", "cgSpark_Points_To_Set_Flowing_Options");
		
		String desccgSpark_Points_To_Set_Flowing_Options = "";	
		if (desccgSpark_Points_To_Set_Flowing_Options.length() > 0) {
			Label descLabelcgSpark_Points_To_Set_Flowing_Options = new Label(editGroupcgSpark_Points_To_Set_Flowing_Options, SWT.WRAP);
			descLabelcgSpark_Points_To_Set_Flowing_Options.setText(desccgSpark_Points_To_Set_Flowing_Options);
		}
		OptionData [] data;	
		

		
		data = new OptionData [] {
		
		new OptionData("Iter",
		"iter",
		"\nIter is a simple, iterative algorithm, which propagates \neverything until the graph does not change. ",
		
		false),
		
		new OptionData("Worklist",
		"worklist",
		"\nWorklist is a worklist-based algorithm that tries to do as \nlittle work as possible. This is currently the fastest \nalgorithm. ",
		
		true),
		
		new OptionData("Cycle",
		"cycle",
		"\nThis algorithm finds cycles in the PAG on-the-fly. It is not yet \nfinished.",
		
		false),
		
		new OptionData("Merge",
		"merge",
		"\nMerge is an algorithm that merges all concrete field (yellow) \nnodes with their corresponding field reference (red) nodes. This \nalgorithm is not yet finished. ",
		
		false),
		
		new OptionData("Alias",
		"alias",
		"\nAlias is an alias-edge based algorithm. This algorithm tends to \ntake the least memory for very large problems, because it does \nnot represent explicitly points-to sets of fields of heap \nobjects. ",
		
		false),
		
		new OptionData("None",
		"none",
		"\nNone means that propagation is not done; the graph is only \nbuilt and simplified. This is useful if an external solver is \nbeing used to perform the propagation. ",
		
		false),
		
		};
		
										
		setcgcg_sparkpropagator_widget(new MultiOptionWidget(editGroupcgSpark_Points_To_Set_Flowing_Options, SWT.NONE, data, new OptionData("Propagator", "p", "cg.spark","propagator", "\nThis option tells Spark which propagation algorithm to use. \n")));
		
		defKey = "p"+" "+"cg.spark"+" "+"propagator";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getcgcg_sparkpropagator_widget().setDef(defaultString);
		}
		
		
		
		data = new OptionData [] {
		
		new OptionData("Hash set",
		"hash",
		"\nHash is an implementation based on Java's built-in hash-set. ",
		
		false),
		
		new OptionData("Bit-vector",
		"bit",
		"\nBit is an implementation using a bit vector. ",
		
		false),
		
		new OptionData("Hybrid",
		"hybrid",
		"\nHybrid is an implementation that keeps an explicit list of up \nto 16 elements, and switches to a bit-vector when the set gets \nlarger than this. ",
		
		false),
		
		new OptionData("Sorted array",
		"array",
		"\nArray is an implementation that keeps the elements of the \npoints-to set in a sorted array. Set membership is tested using \nbinary search, and set union and intersection are computed using \nan algorithm based on the merge step from merge sort. ",
		
		false),
		
		new OptionData("Double",
		"double",
		"\nDouble is an implementation that itself uses a pair of sets for \neach points-to set. The first set in the pair stores new \npointed-to objects that have not yet been propagated, while the \nsecond set stores old pointed-to objects that have been \npropagated and need not be reconsidered. This allows the \npropagation algorithms to be incremental, often speeding them up \nsignificantly. ",
		
		true),
		
		new OptionData("Shared bit-vector",
		"shared",
		"\nThis is a bit-vector representation, in which duplicate \nbit-vectors are found and stored only once to save memory.",
		
		false),
		
		};
		
										
		setcgcg_sparkset_impl_widget(new MultiOptionWidget(editGroupcgSpark_Points_To_Set_Flowing_Options, SWT.NONE, data, new OptionData("Set Implementation", "p", "cg.spark","set-impl", "\nSelects an implementation of a points-to set that Spark should \nuse. ")));
		
		defKey = "p"+" "+"cg.spark"+" "+"set-impl";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getcgcg_sparkset_impl_widget().setDef(defaultString);
		}
		
		
		
		data = new OptionData [] {
		
		new OptionData("Hash set",
		"hash",
		"\nHash is an implementation based on Java's built-in hash-set. ",
		
		false),
		
		new OptionData("Bit-vector",
		"bit",
		"\nBit is an implementation using a bit vector. ",
		
		false),
		
		new OptionData("Hybrid",
		"hybrid",
		"\nHybrid is an implementation that keeps an explicit list of up \nto 16 elements, and switches to a bit-vector when the set gets \nlarger than this. ",
		
		true),
		
		new OptionData("Sorted array",
		"array",
		"\nArray is an implementation that keeps the elements of the \npoints-to set in a sorted array. Set membership is tested using \nbinary search, and set union and intersection are computed using \nan algorithm based on the merge step from merge sort. ",
		
		false),
		
		new OptionData("Shared bit-vector",
		"shared",
		"\nThis is a bit-vector representation, in which duplicate \nbit-vectors are found and stored only once to save memory.",
		
		false),
		
		};
		
										
		setcgcg_sparkdouble_set_old_widget(new MultiOptionWidget(editGroupcgSpark_Points_To_Set_Flowing_Options, SWT.NONE, data, new OptionData("Double Set Old", "p", "cg.spark","double-set-old", "\nSelects an implementation for the sets of old objects in the \ndouble points-to set implementation. This option has no effect \nunless set-impl is set to double. ")));
		
		defKey = "p"+" "+"cg.spark"+" "+"double-set-old";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getcgcg_sparkdouble_set_old_widget().setDef(defaultString);
		}
		
		
		
		data = new OptionData [] {
		
		new OptionData("Hash set",
		"hash",
		"\nHash is an implementation based on Java's built-in hash-set. ",
		
		false),
		
		new OptionData("Bit-vector",
		"bit",
		"\nBit is an implementation using a bit vector. ",
		
		false),
		
		new OptionData("Hybrid",
		"hybrid",
		"\nHybrid is an implementation that keeps an explicit list of up \nto 16 elements, and switches to a bit-vector when the set gets \nlarger than this. ",
		
		true),
		
		new OptionData("Sorted array",
		"array",
		"\nArray is an implementation that keeps the elements of the \npoints-to set in a sorted array. Set membership is tested using \nbinary search, and set union and intersection are computed using \nan algorithm based on the merge step from merge sort. ",
		
		false),
		
		new OptionData("Shared bit-vector",
		"shared",
		"\nThis is a bit-vector representation, in which duplicate \nbit-vectors are found and stored only once to save memory.",
		
		false),
		
		};
		
										
		setcgcg_sparkdouble_set_new_widget(new MultiOptionWidget(editGroupcgSpark_Points_To_Set_Flowing_Options, SWT.NONE, data, new OptionData("Double Set New", "p", "cg.spark","double-set-new", "\nSelects an implementation for the sets of new objects in the \ndouble points-to set implementation. This option has no effect \nunless setImpl is set to double. ")));
		
		defKey = "p"+" "+"cg.spark"+" "+"double-set-new";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getcgcg_sparkdouble_set_new_widget().setDef(defaultString);
		}
		
		

		
		return editGroupcgSpark_Points_To_Set_Flowing_Options;
	}



	private Composite cgSpark_Output_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupcgSpark_Output_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupcgSpark_Output_Options.setLayout(layout);
	
	 	editGroupcgSpark_Output_Options.setText("Spark Output Options");
	 	
		editGroupcgSpark_Output_Options.setData("id", "cgSpark_Output_Options");
		
		String desccgSpark_Output_Options = "";	
		if (desccgSpark_Output_Options.length() > 0) {
			Label descLabelcgSpark_Output_Options = new Label(editGroupcgSpark_Output_Options, SWT.WRAP);
			descLabelcgSpark_Output_Options.setText(desccgSpark_Output_Options);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"cg.spark"+" "+"dump-html";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkdump_html_widget(new BooleanOptionWidget(editGroupcgSpark_Output_Options, SWT.NONE, new OptionData("Dump HTML", "p", "cg.spark","dump-html", "\nWhen this option is set to true, a browseable HTML \nrepresentation of the pointer assignment graph is output to a \nfile called pag.jar after the analysis completes. Note that this \nrepresentation is typically very large. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"dump-pag";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkdump_pag_widget(new BooleanOptionWidget(editGroupcgSpark_Output_Options, SWT.NONE, new OptionData("Dump PAG", "p", "cg.spark","dump-pag", "\nWhen this option is set to true, a representation of the \npointer assignment graph suitable for processing with other \nsolvers (such as the BDD-based solver) is output before the \nanalysis begins. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"dump-solution";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkdump_solution_widget(new BooleanOptionWidget(editGroupcgSpark_Output_Options, SWT.NONE, new OptionData("Dump Solution", "p", "cg.spark","dump-solution", "\nWhen this option is set to true, a representation of the \nresulting points-to sets is dumped. The format is similar to \nthat of the dump-pag option, and is therefore suitable for \ncomparison with the results of other solvers. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"topo-sort";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparktopo_sort_widget(new BooleanOptionWidget(editGroupcgSpark_Output_Options, SWT.NONE, new OptionData("Topological Sort", "p", "cg.spark","topo-sort", "\nWhen this option is set to true, the representation dumped by \nthe dump-pag option is dumped with the variable (green) nodes in \n(pseudo-)topological order. This option has no effect unless \ndump-pag is true. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"dump-types";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparkdump_types_widget(new BooleanOptionWidget(editGroupcgSpark_Output_Options, SWT.NONE, new OptionData("Dump Types", "p", "cg.spark","dump-types", "\nWhen this option is set to true, the representation dumped by \nthe dump-pag option includes type information for all nodes. \nThis option has no effect unless dump-pag is true. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"class-method-var";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setcgcg_sparkclass_method_var_widget(new BooleanOptionWidget(editGroupcgSpark_Output_Options, SWT.NONE, new OptionData("Class Method Var", "p", "cg.spark","class-method-var", "\nWhen this option is set to true, the representation dumped by \nthe dump-pag option represents nodes by numbering each class, \nmethod, and variable within the method separately, rather than \nassigning a single integer to each node. This option has no \neffect unless dump-pag is true. Setting class-method-var to true \nhas the effect of setting topo-sort to false. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"dump-answer";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkdump_answer_widget(new BooleanOptionWidget(editGroupcgSpark_Output_Options, SWT.NONE, new OptionData("Dump Answer", "p", "cg.spark","dump-answer", "\nWhen this option is set to true, the computed reaching types \nfor each variable are dumped to a file, so that they can be \ncompared with the results of other analyses (such as the old \nVTA). ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"add-tags";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkadd_tags_widget(new BooleanOptionWidget(editGroupcgSpark_Output_Options, SWT.NONE, new OptionData("Add Tags", "p", "cg.spark","add-tags", "\nWhen this option is set to true, the results of the \nanalysis are encoded inside tags, and printed with the resulting \nJimple code. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"cg.spark"+" "+"set-mass";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setcgcg_sparkset_mass_widget(new BooleanOptionWidget(editGroupcgSpark_Output_Options, SWT.NONE, new OptionData("Calculate Set Mass", "p", "cg.spark","set-mass", "\nWhen this option is set to true, Spark computes and prints \nvarious cryptic statistics about the size of the points-to sets \ncomputed. ", defaultBool)));
		
		

		
		return editGroupcgSpark_Output_Options;
	}



	private Composite wjtpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupwjtp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupwjtp.setLayout(layout);
	
	 	editGroupwjtp.setText("Whole-Jimple Transformation Pack");
	 	
		editGroupwjtp.setData("id", "wjtp");
		
		String descwjtp = "";	
		if (descwjtp.length() > 0) {
			Label descLabelwjtp = new Label(editGroupwjtp, SWT.WRAP);
			descLabelwjtp.setText(descwjtp);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"wjtp"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjtpenabled_widget(new BooleanOptionWidget(editGroupwjtp, SWT.NONE, new OptionData("Enabled", "p", "wjtp","enabled", "\n", defaultBool)));
		
		

		
		return editGroupwjtp;
	}



	private Composite wjopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupwjop = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupwjop.setLayout(layout);
	
	 	editGroupwjop.setText("Whole-Jimple Optimization Pack");
	 	
		editGroupwjop.setData("id", "wjop");
		
		String descwjop = "";	
		if (descwjop.length() > 0) {
			Label descLabelwjop = new Label(editGroupwjop, SWT.WRAP);
			descLabelwjop.setText(descwjop);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"wjop"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setwjopenabled_widget(new BooleanOptionWidget(editGroupwjop, SWT.NONE, new OptionData("Enabled", "p", "wjop","enabled", "\n", defaultBool)));
		
		

		
		return editGroupwjop;
	}



	private Composite wjopwjop_smbCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupwjopwjop_smb = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupwjopwjop_smb.setLayout(layout);
	
	 	editGroupwjopwjop_smb.setText("Static Method Binding");
	 	
		editGroupwjopwjop_smb.setData("id", "wjopwjop_smb");
		
		String descwjopwjop_smb = "";	
		if (descwjopwjop_smb.length() > 0) {
			Label descLabelwjopwjop_smb = new Label(editGroupwjopwjop_smb, SWT.WRAP);
			descLabelwjopwjop_smb.setText(descwjopwjop_smb);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"wjop.smb"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setwjopwjop_smbenabled_widget(new BooleanOptionWidget(editGroupwjopwjop_smb, SWT.NONE, new OptionData("Enabled", "p", "wjop.smb","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"wjop.smb"+" "+"insert-null-checks";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_smbinsert_null_checks_widget(new BooleanOptionWidget(editGroupwjopwjop_smb, SWT.NONE, new OptionData("Insert Null Checks", "p", "wjop.smb","insert-null-checks", "\nThe receiver object is checked for nullness before the target \nmethod is invoked. If the target is null, then a NullPointer \nexception is thrown. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"wjop.smb"+" "+"insert-redundant-casts";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_smbinsert_redundant_casts_widget(new BooleanOptionWidget(editGroupwjopwjop_smb, SWT.NONE, new OptionData("Insert Redundant Casts", "p", "wjop.smb","insert-redundant-casts", "\nInserts extra casts for the verifier. The verifier will \ncomplain if the target uses `this' (so we have to pass an extra \nparameter), and the argument passed to the method is not the \nsame type. For instance, Bottle.pricestatic is a method which \ntakes a Cost object, and Cost is an interface implemented by \nBottle. We must then cast the Cost to a Bottle before passing \nit to pricestatic. ", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Unsafe",
		"unsafe",
		"\n",
		
		true),
		
		new OptionData("Safe",
		"safe",
		"\n",
		
		false),
		
		new OptionData("None",
		"none",
		"\n",
		
		false),
		
		};
		
										
		setwjopwjop_smballowed_modifier_changes_widget(new MultiOptionWidget(editGroupwjopwjop_smb, SWT.NONE, data, new OptionData("Allow Modifier Changes", "p", "wjop.smb","allowed-modifier-changes", "\nDetermines what changes in visibility modifiers are allowed. \n``unsafe'' modifies the visibility on code so that all inlining \nis permitted; some IllegalAccessErrors may be missed. ``safe'' \npreserves the exact meaning of the analysed program, and \n``none'' changes no modifiers whatsoever. \n")));
		
		defKey = "p"+" "+"wjop.smb"+" "+"allowed-modifier-changes";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getwjopwjop_smballowed_modifier_changes_widget().setDef(defaultString);
		}
		
		

		
		return editGroupwjopwjop_smb;
	}



	private Composite wjopwjop_siCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupwjopwjop_si = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupwjopwjop_si.setLayout(layout);
	
	 	editGroupwjopwjop_si.setText("Static Inlining");
	 	
		editGroupwjopwjop_si.setData("id", "wjopwjop_si");
		
		String descwjopwjop_si = "";	
		if (descwjopwjop_si.length() > 0) {
			Label descLabelwjopwjop_si = new Label(editGroupwjopwjop_si, SWT.WRAP);
			descLabelwjopwjop_si.setText(descwjopwjop_si);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"wjop.si"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_sienabled_widget(new BooleanOptionWidget(editGroupwjopwjop_si, SWT.NONE, new OptionData("Enabled", "p", "wjop.si","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"wjop.si"+" "+"insert-null-checks";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_siinsert_null_checks_widget(new BooleanOptionWidget(editGroupwjopwjop_si, SWT.NONE, new OptionData("Insert Null Checks", "p", "wjop.si","insert-null-checks", "\nAs in StaticMethodBinder.", defaultBool)));
		
		
		
		defKey = "p"+" "+"wjop.si"+" "+"insert-redundant-casts";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjopwjop_siinsert_redundant_casts_widget(new BooleanOptionWidget(editGroupwjopwjop_si, SWT.NONE, new OptionData("Insert Redundant Casts", "p", "wjop.si","insert-redundant-casts", "\nAs in StaticMethodBinder.", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Unsafe",
		"unsafe",
		"\n",
		
		true),
		
		new OptionData("Safe",
		"safe",
		"\n",
		
		false),
		
		new OptionData("None",
		"none",
		"\n",
		
		false),
		
		};
		
										
		setwjopwjop_siallowed_modifier_changes_widget(new MultiOptionWidget(editGroupwjopwjop_si, SWT.NONE, data, new OptionData("Allow Modifier Changes", "p", "wjop.si","allowed-modifier-changes", "\nAs in StaticMethodBinder.")));
		
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

		setwjopwjop_siexpansion_factor_widget(new StringOptionWidget(editGroupwjopwjop_si, SWT.NONE, new OptionData("Expansion Factor",  "p", "wjop.si","expansion-factor", "\nDetermines the maximum allowed expansion of a method. Inlining \nwill cause the method to grow by a factor of no more than \nexpansion-factor. ", defaultString)));
		
		
		defKey = "p"+" "+"wjop.si"+" "+"max-container-size";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "5000";
			
		}

		setwjopwjop_simax_container_size_widget(new StringOptionWidget(editGroupwjopwjop_si, SWT.NONE, new OptionData("Max Container Size",  "p", "wjop.si","max-container-size", "\nDetermines the maximum number of Jimple statements for a \ncontainer method. If a method has more than this number of \nJimple statements, then no methods will be inlined into it. \n", defaultString)));
		
		
		defKey = "p"+" "+"wjop.si"+" "+"max-inlinee-size";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "20";
			
		}

		setwjopwjop_simax_inlinee_size_widget(new StringOptionWidget(editGroupwjopwjop_si, SWT.NONE, new OptionData("Max Inlinee Size",  "p", "wjop.si","max-inlinee-size", "\nDetermines the maximum number of Jimple statements for an \ninlinee method. If a method has more than this number of Jimple \nstatements, then it will not be inlined into other methods. \n", defaultString)));
		

		
		return editGroupwjopwjop_si;
	}



	private Composite wjapCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupwjap = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupwjap.setLayout(layout);
	
	 	editGroupwjap.setText("Whole-Jimple Annotation Pack");
	 	
		editGroupwjap.setData("id", "wjap");
		
		String descwjap = "";	
		if (descwjap.length() > 0) {
			Label descLabelwjap = new Label(editGroupwjap, SWT.WRAP);
			descLabelwjap.setText(descwjap);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"wjap"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setwjapenabled_widget(new BooleanOptionWidget(editGroupwjap, SWT.NONE, new OptionData("Enabled", "p", "wjap","enabled", "\nSome analyses do not transform Jimple body directly, but \nannotate statements or values with tags. Whole-Jimple annotation \npack provides a place for annotation-oriented analysis in a \nwhole program mode. 					", defaultBool)));
		
		

		
		return editGroupwjap;
	}



	private Composite wjapwjap_raCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupwjapwjap_ra = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupwjapwjap_ra.setLayout(layout);
	
	 	editGroupwjapwjap_ra.setText("Rectangular Array Finder");
	 	
		editGroupwjapwjap_ra.setData("id", "wjapwjap_ra");
		
		String descwjapwjap_ra = "";	
		if (descwjapwjap_ra.length() > 0) {
			Label descLabelwjapwjap_ra = new Label(editGroupwjapwjap_ra, SWT.WRAP);
			descLabelwjapwjap_ra.setText(descwjapwjap_ra);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"wjap.ra"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setwjapwjap_raenabled_widget(new BooleanOptionWidget(editGroupwjapwjap_ra, SWT.NONE, new OptionData("Enabled", "p", "wjap.ra","enabled", "\nIn Java, a multi-dimensional array is an array of arrays, \nwhich means the shape of the array can be ragged. However, many \napplications use rectangular arrays. Such information is very \nhelpful in proving safe array bounds checks. Rectangular array \nfinder traverses Jimple statements based on static call graph, \nand finds array variables always holding rectangular \ntwo-dimensional array objects. The analysis results are used by \narray bounds check elimination (jap.abc). This phase does not \nchange the program. 						", defaultBool)));
		
		

		
		return editGroupwjapwjap_ra;
	}



	private Composite shimpleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupshimple = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupshimple.setLayout(layout);
	
	 	editGroupshimple.setText("Shimple Phase Options");
	 	
		editGroupshimple.setData("id", "shimple");
		
		String descshimple = "General Shimple options.";	
		if (descshimple.length() > 0) {
			Label descLabelshimple = new Label(editGroupshimple, SWT.WRAP);
			descLabelshimple.setText(descshimple);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"shimple"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setshimpleenabled_widget(new BooleanOptionWidget(editGroupshimple, SWT.NONE, new OptionData("Enabled", "p", "shimple","enabled", "\n", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Naive Phi Node Elimination",
		"none",
		"\nIf enabled, neither pre-optimization nor \npost-optimization will be applied to the Phi elimination \nprocess. This is useful for monitoring and understanding \nthe behaviour of Shimple optimizations or \ntransformations.",
		
		false),
		
		new OptionData("Pre-optimize Phi Elimination",
		"pre",
		"\nIf enabled, some recommended optimizations such as \ndead code elimination and local packing are applied \nbefore Phi node elimination. This does not appear to be as \neffective as post-optimization, but the option is \nprovided for future testing and investigation.",
		
		false),
		
		new OptionData("Post-optimize Phi Elimination",
		"post",
		"\nIf enabled, applies recommended optimizations such \nas dead code elimination and local packing after Phi \nnode elimination. This appears to be more effective than \npre-optimization.",
		
		true),
		
		new OptionData("Pre- and Post- Optimize Phi Elimination",
		"pre-and-post",
		"\nIf enabled, applies recommended optimizations such \nas dead code elimination and local packing both \nbefore and after Phi node elimination. Provided for \nexperimentation.",
		
		false),
		
		};
		
										
		setshimplephi_elim_opt_widget(new MultiOptionWidget(editGroupshimple, SWT.NONE, data, new OptionData("Phi Node Elimination Optimizations", "p", "shimple","phi-elim-opt", "\nThese options control Shimple's behaviour when \neliminating Phi nodes.")));
		
		defKey = "p"+" "+"shimple"+" "+"phi-elim-opt";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getshimplephi_elim_opt_widget().setDef(defaultString);
		}
		
		

		
		return editGroupshimple;
	}



	private Composite stpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupstp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupstp.setLayout(layout);
	
	 	editGroupstp.setText("Shimple Transformation Pack");
	 	
		editGroupstp.setData("id", "stp");
		
		String descstp = "";	
		if (descstp.length() > 0) {
			Label descLabelstp = new Label(editGroupstp, SWT.WRAP);
			descLabelstp.setText(descstp);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"stp"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setstpenabled_widget(new BooleanOptionWidget(editGroupstp, SWT.NONE, new OptionData("Enabled", "p", "stp","enabled", "\nIf the Shimple phase is enabled, Soot applies the \ncontents of the Shimple Transformation Pack to each method \nunder analysis. This pack contains no transformations in an \nunmodified version of Soot.", defaultBool)));
		
		

		
		return editGroupstp;
	}



	private Composite sopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupsop = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupsop.setLayout(layout);
	
	 	editGroupsop.setText("Shimple Optimization Pack");
	 	
		editGroupsop.setData("id", "sop");
		
		String descsop = "";	
		if (descsop.length() > 0) {
			Label descLabelsop = new Label(editGroupsop, SWT.WRAP);
			descLabelsop.setText(descsop);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"sop"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setsopenabled_widget(new BooleanOptionWidget(editGroupsop, SWT.NONE, new OptionData("Enabled", "p", "sop","enabled", "\nWhen enabled, Soot applies the Shimple Optimization \nPack to every ShimpleBody in application classes. \nThis section lists the default transformations in the \nShimple Optimization Pack.", defaultBool)));
		
		

		
		return editGroupsop;
	}



	private Composite sopsop_cpfCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupsopsop_cpf = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupsopsop_cpf.setLayout(layout);
	
	 	editGroupsopsop_cpf.setText("Constant Propagator and Folder");
	 	
		editGroupsopsop_cpf.setData("id", "sopsop_cpf");
		
		String descsopsop_cpf = "";	
		if (descsopsop_cpf.length() > 0) {
			Label descLabelsopsop_cpf = new Label(editGroupsopsop_cpf, SWT.WRAP);
			descLabelsopsop_cpf.setText(descsopsop_cpf);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"sop.cpf"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setsopsop_cpfenabled_widget(new BooleanOptionWidget(editGroupsopsop_cpf, SWT.NONE, new OptionData("Enabled", "p", "sop.cpf","enabled", "\nAn example implementation of constant 						propagation using \nShimple. Informal tests show that this 						analysis is already \nmore powerful than the simplistic \n						ConstantPropagatorAndFolder optimization provided by \n						Jimple, particularly when control flow is involved. This \n						optimization demonstrates some of the benefits of SSA -- \n						particularly the fact that Phi nodes represent natural \n						merge points in the control flow. This implementation \n						also demonstrates how to access U/D and D/U chains in \n						Shimple.", defaultBool)));
		
		

		
		return editGroupsopsop_cpf;
	}



	private Composite jtpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjtp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjtp.setLayout(layout);
	
	 	editGroupjtp.setText("Jimple Transformation Pack");
	 	
		editGroupjtp.setData("id", "jtp");
		
		String descjtp = "";	
		if (descjtp.length() > 0) {
			Label descLabeljtp = new Label(editGroupjtp, SWT.WRAP);
			descLabeljtp.setText(descjtp);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jtp"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjtpenabled_widget(new BooleanOptionWidget(editGroupjtp, SWT.NONE, new OptionData("Enabled", "p", "jtp","enabled", "\nSoot applies the contents of the Jimple Transformation Pack to \neach method under analysis. This pack contains no \ntransformations in an unmodified version of Soot. ", defaultBool)));
		
		

		
		return editGroupjtp;
	}



	private Composite jopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjop = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjop.setLayout(layout);
	
	 	editGroupjop.setText("Jimple Optimization Pack");
	 	
		editGroupjop.setData("id", "jop");
		
		String descjop = "";	
		if (descjop.length() > 0) {
			Label descLabeljop = new Label(editGroupjop, SWT.WRAP);
			descLabeljop.setText(descjop);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopenabled_widget(new BooleanOptionWidget(editGroupjop, SWT.NONE, new OptionData("Enabled", "p", "jop","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjop;
	}



	private Composite jopjop_cseCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_cse = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_cse.setLayout(layout);
	
	 	editGroupjopjop_cse.setText("Common Subexpression Eliminator");
	 	
		editGroupjopjop_cse.setData("id", "jopjop_cse");
		
		String descjopjop_cse = "";	
		if (descjopjop_cse.length() > 0) {
			Label descLabeljopjop_cse = new Label(editGroupjopjop_cse, SWT.WRAP);
			descLabeljopjop_cse.setText(descjopjop_cse);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.cse"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_cseenabled_widget(new BooleanOptionWidget(editGroupjopjop_cse, SWT.NONE, new OptionData("Enabled", "p", "jop.cse","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.cse"+" "+"naive-side-effect";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_csenaive_side_effect_widget(new BooleanOptionWidget(editGroupjopjop_cse, SWT.NONE, new OptionData("Naive Side Effect Tester", "p", "jop.cse","naive-side-effect", "\nIf naive-side-effect is set to true the Common Subexpression \nEliminator uses the conservative side effect information \nprovided by the NaiveSideEffectTester class, even if \ninterprocedural information about side effects is available. The \nnaive side effect analysis is based solely on the information \navailable locally about a statement. It assumes, for example, \nthat any method call has the potential to write and read all \ninstance and static fields in the program. If naive-side-effect \nis set to false and whole program analysis has been specified by \nthe -W or -whole-program options, then the Common Subexpression \nEliminator uses the side effect information provided by the \nPASideEffectTester class. PASideEffectTester uses the \ninformation provided by a points-to analysis to determine which \nfields and statics may be written or read by a given statement. \nIf whole program analysis is not performed, naive side effect \ninformation is used regardless of the setting of \nnaive-side-effect. ", defaultBool)));
		
		

		
		return editGroupjopjop_cse;
	}



	private Composite jopjop_bcmCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_bcm = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_bcm.setLayout(layout);
	
	 	editGroupjopjop_bcm.setText("Busy Code Motion");
	 	
		editGroupjopjop_bcm.setData("id", "jopjop_bcm");
		
		String descjopjop_bcm = "";	
		if (descjopjop_bcm.length() > 0) {
			Label descLabeljopjop_bcm = new Label(editGroupjopjop_bcm, SWT.WRAP);
			descLabeljopjop_bcm.setText(descjopjop_bcm);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.bcm"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_bcmenabled_widget(new BooleanOptionWidget(editGroupjopjop_bcm, SWT.NONE, new OptionData("Enabled", "p", "jop.bcm","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.bcm"+" "+"naive-side-effect";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_bcmnaive_side_effect_widget(new BooleanOptionWidget(editGroupjopjop_bcm, SWT.NONE, new OptionData("Naive Side Effect Tester", "p", "jop.bcm","naive-side-effect", "\nIf naive-side-effect is set to true Busy Code Motion uses the \nconservative side effect information provided by the \nNaiveSideEffectTester class, even if interprocedural information \nabout side effects is available. The naive side effect analysis \nis based solely on the information available locally about a \nstatement. It assumes, for example, that any method call has the \npotential to write and read all instance and static fields in \nthe program. If naive-side-effect is set to false and whole \nprogram analysis has been specified by the -W or -whole-program \noptions, then Busy Code Motion uses the side effect information \nprovided by the PASideEffectTester class. PASideEffectTester \nuses the information provided by a points-to analysis to \ndetermine which fields and statics may be written or read by a \ngiven statement. If whole program analysis is not performed, \nnaive side effect information is used regardless of the setting \nof naive-side-effect. ", defaultBool)));
		
		

		
		return editGroupjopjop_bcm;
	}



	private Composite jopjop_lcmCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_lcm = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_lcm.setLayout(layout);
	
	 	editGroupjopjop_lcm.setText("Lazy Code Motion");
	 	
		editGroupjopjop_lcm.setData("id", "jopjop_lcm");
		
		String descjopjop_lcm = "";	
		if (descjopjop_lcm.length() > 0) {
			Label descLabeljopjop_lcm = new Label(editGroupjopjop_lcm, SWT.WRAP);
			descLabeljopjop_lcm.setText(descjopjop_lcm);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.lcm"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_lcmenabled_widget(new BooleanOptionWidget(editGroupjopjop_lcm, SWT.NONE, new OptionData("Enabled", "p", "jop.lcm","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.lcm"+" "+"unroll";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_lcmunroll_widget(new BooleanOptionWidget(editGroupjopjop_lcm, SWT.NONE, new OptionData("Unroll", "p", "jop.lcm","unroll", "\nIf true, perform loop inversion before doing the \ntransformation. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.lcm"+" "+"naive-side-effect";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_lcmnaive_side_effect_widget(new BooleanOptionWidget(editGroupjopjop_lcm, SWT.NONE, new OptionData("Naive Side Effect Tester", "p", "jop.lcm","naive-side-effect", "\nIf naive-side-effect is set to true Lazy Code Motion uses the \nconservative side effect information provided by the \nNaiveSideEffectTester class, even if interprocedural information \nabout side effects is available. The naive side effect analysis \nis based solely on the information available locally about a \nstatement. It assumes, for example, that any method call has the \npotential to write and read all instance and static fields in \nthe program. If naive-side-effect is set to false and whole \nprogram analysis has been specified by the -W or -whole-program \noptions, then Lazy Code Motion uses the side effect information \nprovided by the PASideEffectTester class. PASideEffectTester \nuses the information provided by a points-to analysis to \ndetermine which fields and statics may be written or read by a \ngiven statement. If whole program analysis is not performed, \nnaive side effect information is used regardless of the setting \nof naive-side-effect. ", defaultBool)));
		
		
		
		data = new OptionData [] {
		
		new OptionData("Safe",
		"safe",
		"\nSafe, but only considers moving additions, subtractions and \nmultiplications. ",
		
		true),
		
		new OptionData("Medium",
		"medium",
		"\nUnsafe in multi-threaded programs, as it may reuse the values \nread from field accesses. ",
		
		false),
		
		new OptionData("Unsafe",
		"unsafe",
		"\nMay violate Java's exception semantics, as it may move or \nreorder exception-throwing statements, potentially outside of \ntry-catch blocks. ",
		
		false),
		
		};
		
										
		setjopjop_lcmsafety_widget(new MultiOptionWidget(editGroupjopjop_lcm, SWT.NONE, data, new OptionData("Safety", "p", "jop.lcm","safety", "\nThis option controls which fields and statements are candidates \nfor code motion. ")));
		
		defKey = "p"+" "+"jop.lcm"+" "+"safety";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);
		
			getjopjop_lcmsafety_widget().setDef(defaultString);
		}
		
		

		
		return editGroupjopjop_lcm;
	}



	private Composite jopjop_cpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_cp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_cp.setLayout(layout);
	
	 	editGroupjopjop_cp.setText("Copy Propagator");
	 	
		editGroupjopjop_cp.setData("id", "jopjop_cp");
		
		String descjopjop_cp = "Removes unnecessary copies";	
		if (descjopjop_cp.length() > 0) {
			Label descLabeljopjop_cp = new Label(editGroupjopjop_cp, SWT.WRAP);
			descLabeljopjop_cp.setText(descjopjop_cp);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.cp"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_cpenabled_widget(new BooleanOptionWidget(editGroupjopjop_cp, SWT.NONE, new OptionData("Enabled", "p", "jop.cp","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.cp"+" "+"only-regular-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_cponly_regular_locals_widget(new BooleanOptionWidget(editGroupjopjop_cp, SWT.NONE, new OptionData("Only Regular Locals", "p", "jop.cp","only-regular-locals", "\nOnly propagate copies through ``regular'' locals, that is, \nthose declared in the source bytecode. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.cp"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_cponly_stack_locals_widget(new BooleanOptionWidget(editGroupjopjop_cp, SWT.NONE, new OptionData("Only Stack Locals", "p", "jop.cp","only-stack-locals", "\nOnly propagate copies through locals that represent stack \nlocations in the original bytecode. ", defaultBool)));
		
		

		
		return editGroupjopjop_cp;
	}



	private Composite jopjop_cpfCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_cpf = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_cpf.setLayout(layout);
	
	 	editGroupjopjop_cpf.setText("Constant Propagator and Folder");
	 	
		editGroupjopjop_cpf.setData("id", "jopjop_cpf");
		
		String descjopjop_cpf = "";	
		if (descjopjop_cpf.length() > 0) {
			Label descLabeljopjop_cpf = new Label(editGroupjopjop_cpf, SWT.WRAP);
			descLabeljopjop_cpf.setText(descjopjop_cpf);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.cpf"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_cpfenabled_widget(new BooleanOptionWidget(editGroupjopjop_cpf, SWT.NONE, new OptionData("Enabled", "p", "jop.cpf","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjopjop_cpf;
	}



	private Composite jopjop_cbfCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_cbf = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_cbf.setLayout(layout);
	
	 	editGroupjopjop_cbf.setText("Conditional Branch Folder");
	 	
		editGroupjopjop_cbf.setData("id", "jopjop_cbf");
		
		String descjopjop_cbf = "";	
		if (descjopjop_cbf.length() > 0) {
			Label descLabeljopjop_cbf = new Label(editGroupjopjop_cbf, SWT.WRAP);
			descLabeljopjop_cbf.setText(descjopjop_cbf);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.cbf"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_cbfenabled_widget(new BooleanOptionWidget(editGroupjopjop_cbf, SWT.NONE, new OptionData("Enabled", "p", "jop.cbf","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjopjop_cbf;
	}



	private Composite jopjop_daeCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_dae = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_dae.setLayout(layout);
	
	 	editGroupjopjop_dae.setText("Dead Assignment Eliminator");
	 	
		editGroupjopjop_dae.setData("id", "jopjop_dae");
		
		String descjopjop_dae = "";	
		if (descjopjop_dae.length() > 0) {
			Label descLabeljopjop_dae = new Label(editGroupjopjop_dae, SWT.WRAP);
			descLabeljopjop_dae.setText(descjopjop_dae);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.dae"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_daeenabled_widget(new BooleanOptionWidget(editGroupjopjop_dae, SWT.NONE, new OptionData("Enabled", "p", "jop.dae","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jop.dae"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjopjop_daeonly_stack_locals_widget(new BooleanOptionWidget(editGroupjopjop_dae, SWT.NONE, new OptionData("Only Stack Locals", "p", "jop.dae","only-stack-locals", "\n", defaultBool)));
		
		

		
		return editGroupjopjop_dae;
	}



	private Composite jopjop_uce1Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_uce1 = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_uce1.setLayout(layout);
	
	 	editGroupjopjop_uce1.setText("Unreachable Code Eliminator 1");
	 	
		editGroupjopjop_uce1.setData("id", "jopjop_uce1");
		
		String descjopjop_uce1 = "";	
		if (descjopjop_uce1.length() > 0) {
			Label descLabeljopjop_uce1 = new Label(editGroupjopjop_uce1, SWT.WRAP);
			descLabeljopjop_uce1.setText(descjopjop_uce1);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.uce1"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_uce1enabled_widget(new BooleanOptionWidget(editGroupjopjop_uce1, SWT.NONE, new OptionData("Enabled", "p", "jop.uce1","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjopjop_uce1;
	}



	private Composite jopjop_ubf1Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_ubf1 = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_ubf1.setLayout(layout);
	
	 	editGroupjopjop_ubf1.setText("Unconditional Branch Folder 1");
	 	
		editGroupjopjop_ubf1.setData("id", "jopjop_ubf1");
		
		String descjopjop_ubf1 = "";	
		if (descjopjop_ubf1.length() > 0) {
			Label descLabeljopjop_ubf1 = new Label(editGroupjopjop_ubf1, SWT.WRAP);
			descLabeljopjop_ubf1.setText(descjopjop_ubf1);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.ubf1"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_ubf1enabled_widget(new BooleanOptionWidget(editGroupjopjop_ubf1, SWT.NONE, new OptionData("Enabled", "p", "jop.ubf1","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjopjop_ubf1;
	}



	private Composite jopjop_uce2Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_uce2 = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_uce2.setLayout(layout);
	
	 	editGroupjopjop_uce2.setText("Unreachable Code Eliminator 2");
	 	
		editGroupjopjop_uce2.setData("id", "jopjop_uce2");
		
		String descjopjop_uce2 = "";	
		if (descjopjop_uce2.length() > 0) {
			Label descLabeljopjop_uce2 = new Label(editGroupjopjop_uce2, SWT.WRAP);
			descLabeljopjop_uce2.setText(descjopjop_uce2);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.uce2"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_uce2enabled_widget(new BooleanOptionWidget(editGroupjopjop_uce2, SWT.NONE, new OptionData("Enabled", "p", "jop.uce2","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjopjop_uce2;
	}



	private Composite jopjop_ubf2Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_ubf2 = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_ubf2.setLayout(layout);
	
	 	editGroupjopjop_ubf2.setText("Unconditional Branch Folder 2");
	 	
		editGroupjopjop_ubf2.setData("id", "jopjop_ubf2");
		
		String descjopjop_ubf2 = "";	
		if (descjopjop_ubf2.length() > 0) {
			Label descLabeljopjop_ubf2 = new Label(editGroupjopjop_ubf2, SWT.WRAP);
			descLabeljopjop_ubf2.setText(descjopjop_ubf2);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.ubf2"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_ubf2enabled_widget(new BooleanOptionWidget(editGroupjopjop_ubf2, SWT.NONE, new OptionData("Enabled", "p", "jop.ubf2","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjopjop_ubf2;
	}



	private Composite jopjop_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjopjop_ule = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjopjop_ule.setLayout(layout);
	
	 	editGroupjopjop_ule.setText("Unused Local Eliminator");
	 	
		editGroupjopjop_ule.setData("id", "jopjop_ule");
		
		String descjopjop_ule = "Removes unused locals";	
		if (descjopjop_ule.length() > 0) {
			Label descLabeljopjop_ule = new Label(editGroupjopjop_ule, SWT.WRAP);
			descLabeljopjop_ule.setText(descjopjop_ule);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jop.ule"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjopjop_uleenabled_widget(new BooleanOptionWidget(editGroupjopjop_ule, SWT.NONE, new OptionData("Enabled", "p", "jop.ule","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjopjop_ule;
	}



	private Composite japCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjap = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjap.setLayout(layout);
	
	 	editGroupjap.setText("Jimple Annotation Pack");
	 	
		editGroupjap.setData("id", "jap");
		
		String descjap = "";	
		if (descjap.length() > 0) {
			Label descLabeljap = new Label(editGroupjap, SWT.WRAP);
			descLabeljap.setText(descjap);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jap"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setjapenabled_widget(new BooleanOptionWidget(editGroupjap, SWT.NONE, new OptionData("Enabled", "p", "jap","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjap;
	}



	private Composite japjap_npcCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjapjap_npc = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjapjap_npc.setLayout(layout);
	
	 	editGroupjapjap_npc.setText("Null Pointer Check Options");
	 	
		editGroupjapjap_npc.setData("id", "japjap_npc");
		
		String descjapjap_npc = "";	
		if (descjapjap_npc.length() > 0) {
			Label descLabeljapjap_npc = new Label(editGroupjapjap_npc, SWT.WRAP);
			descLabeljapjap_npc.setText(descjapjap_npc);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.npc"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_npcenabled_widget(new BooleanOptionWidget(editGroupjapjap_npc, SWT.NONE, new OptionData("Enabled", "p", "jap.npc","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.npc"+" "+"only-array-ref";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_npconly_array_ref_widget(new BooleanOptionWidget(editGroupjapjap_npc, SWT.NONE, new OptionData("Only Array Ref", "p", "jap.npc","only-array-ref", "\nBy default, all bytecodes that need null pointer checks \nare annotated with the analysis result. When this option is \nset to true, Soot will annotate only array-referencing \nbytecodes with null pointer check information; other bytecodes, \nsuch as getfield and putfield, will not be annotated. \n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.npc"+" "+"profiling";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_npcprofiling_widget(new BooleanOptionWidget(editGroupjapjap_npc, SWT.NONE, new OptionData("Profiling", "p", "jap.npc","profiling", "\nIf this option is true, the analysis inserts profiling \ninstructions counting the number of eliminated safe null pointer \nchecks at runtime. This is only for profiling purpose. 						", defaultBool)));
		
		

		
		return editGroupjapjap_npc;
	}



	private Composite japjap_abcCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjapjap_abc = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjapjap_abc.setLayout(layout);
	
	 	editGroupjapjap_abc.setText("Array Bound Check Options");
	 	
		editGroupjapjap_abc.setData("id", "japjap_abc");
		
		String descjapjap_abc = "";	
		if (descjapjap_abc.length() > 0) {
			Label descLabeljapjap_abc = new Label(editGroupjapjap_abc, SWT.WRAP);
			descLabeljapjap_abc.setText(descjapjap_abc);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.abc"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcenabled_widget(new BooleanOptionWidget(editGroupjapjap_abc, SWT.NONE, new OptionData("Enabled", "p", "jap.abc","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-all";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_all_widget(new BooleanOptionWidget(editGroupjapjap_abc, SWT.NONE, new OptionData("With All", "p", "jap.abc","with-all", "\nA macro. Instead of typing a long string of phase options, \nthis option will turn on all options of the phase ``jap.abc''. \n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-fieldref";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_fieldref_widget(new BooleanOptionWidget(editGroupjapjap_abc, SWT.NONE, new OptionData("With Field References", "p", "jap.abc","with-fieldref", "\nThe analysis treats field references (static and instance) as \ncommon subexpressions. The restrictions from the `with-arrayref' \noption also apply. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-arrayref";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_arrayref_widget(new BooleanOptionWidget(editGroupjapjap_abc, SWT.NONE, new OptionData("With Array References", "p", "jap.abc","with-arrayref", "\nWith this option enabled, array references can be considered as \ncommon subexpressions; however, we are more conservative when \nwriting into an array, because array objects may be aliased. \nNOTE: We also assume that the application in a single-threaded \nprogram or in a synchronized block. That is, an array element \nmay not be changed by other threads between two array \nreferences. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-cse";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_cse_widget(new BooleanOptionWidget(editGroupjapjap_abc, SWT.NONE, new OptionData("With Common Sub-expressions", "p", "jap.abc","with-cse", "\nThe analysis will consider common subexpressions. For example, \nconsider the situation where r1 is assigned a*b; later, r2 \nis assigned a*b, where both a and b have not been changed \nbetween the two statements. The analysis can conclude that r2 \nhas the same value as r1. Experiments show that this option \ncan improve the result slightly. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-classfield";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_classfield_widget(new BooleanOptionWidget(editGroupjapjap_abc, SWT.NONE, new OptionData("With Class Field", "p", "jap.abc","with-classfield", "\nThis option makes the analysis work on the class level. The \nalgorithm analyzes `final' or `private' class fields first. It \ncan recognize the fields that hold array objects with constant \nlength. In an application using lots of array fields, this \noption can improve the analysis results dramatically. \n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"with-rectarray";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcwith_rectarray_widget(new BooleanOptionWidget(editGroupjapjap_abc, SWT.NONE, new OptionData("With Rectangular Array", "p", "jap.abc","with-rectarray", "\nThis option is used together with wjap.ra to make Soot run the \nwhole-program analysis for rectangular array objects. This \nanalysis is based on the call graph, and it usually takes a long \ntime. If the application uses rectangular arrays, these options \ncan improve the analysis result. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.abc"+" "+"profiling";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_abcprofiling_widget(new BooleanOptionWidget(editGroupjapjap_abc, SWT.NONE, new OptionData("Profiling", "p", "jap.abc","profiling", "\n", defaultBool)));
		
		

		
		return editGroupjapjap_abc;
	}



	private Composite japjap_profilingCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjapjap_profiling = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjapjap_profiling.setLayout(layout);
	
	 	editGroupjapjap_profiling.setText("Profiling Generator");
	 	
		editGroupjapjap_profiling.setData("id", "japjap_profiling");
		
		String descjapjap_profiling = "";	
		if (descjapjap_profiling.length() > 0) {
			Label descLabeljapjap_profiling = new Label(editGroupjapjap_profiling, SWT.WRAP);
			descLabeljapjap_profiling.setText(descjapjap_profiling);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.profiling"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_profilingenabled_widget(new BooleanOptionWidget(editGroupjapjap_profiling, SWT.NONE, new OptionData("Enabled", "p", "jap.profiling","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.profiling"+" "+"notmainentry";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_profilingnotmainentry_widget(new BooleanOptionWidget(editGroupjapjap_profiling, SWT.NONE, new OptionData("Not Main Entry", "p", "jap.profiling","notmainentry", "\n", defaultBool)));
		
		

		
		return editGroupjapjap_profiling;
	}



	private Composite japjap_seaCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjapjap_sea = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjapjap_sea.setLayout(layout);
	
	 	editGroupjapjap_sea.setText("Side effect tagger");
	 	
		editGroupjapjap_sea.setData("id", "japjap_sea");
		
		String descjapjap_sea = "";	
		if (descjapjap_sea.length() > 0) {
			Label descLabeljapjap_sea = new Label(editGroupjapjap_sea, SWT.WRAP);
			descLabeljapjap_sea.setText(descjapjap_sea);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.sea"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_seaenabled_widget(new BooleanOptionWidget(editGroupjapjap_sea, SWT.NONE, new OptionData("Enabled", "p", "jap.sea","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.sea"+" "+"naive";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_seanaive_widget(new BooleanOptionWidget(editGroupjapjap_sea, SWT.NONE, new OptionData("Build naive dependence graph", "p", "jap.sea","naive", "\nWhen set to true, the dependence graph is built with a node for \neach statement, without merging the nodes for equivalent \nstatements. The purpose of this switch is to make it possible to \nmeasure the effect of merging nodes for equivalent statements on \nthe size of the dependence graph.", defaultBool)));
		
		

		
		return editGroupjapjap_sea;
	}



	private Composite japjap_fieldrwCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjapjap_fieldrw = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjapjap_fieldrw.setLayout(layout);
	
	 	editGroupjapjap_fieldrw.setText("Field Read/Write Tagger");
	 	
		editGroupjapjap_fieldrw.setData("id", "japjap_fieldrw");
		
		String descjapjap_fieldrw = "";	
		if (descjapjap_fieldrw.length() > 0) {
			Label descLabeljapjap_fieldrw = new Label(editGroupjapjap_fieldrw, SWT.WRAP);
			descLabeljapjap_fieldrw.setText(descjapjap_fieldrw);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.fieldrw"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_fieldrwenabled_widget(new BooleanOptionWidget(editGroupjapjap_fieldrw, SWT.NONE, new OptionData("Enabled", "p", "jap.fieldrw","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"jap.fieldrw"+" "+"threshold";
		defKey = defKey.trim();
		
		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "100";
			
		}

		setjapjap_fieldrwthreshold_widget(new StringOptionWidget(editGroupjapjap_fieldrw, SWT.NONE, new OptionData("Maximum number of fields",  "p", "jap.fieldrw","threshold", "\nIf a statement reads/writes more than this number of fields, no \ntag will be produced for it, in order to keep the size of the \ntags reasonable. ", defaultString)));
		

		
		return editGroupjapjap_fieldrw;
	}



	private Composite japjap_cgtaggerCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupjapjap_cgtagger = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupjapjap_cgtagger.setLayout(layout);
	
	 	editGroupjapjap_cgtagger.setText("Call Graph Tagger");
	 	
		editGroupjapjap_cgtagger.setData("id", "japjap_cgtagger");
		
		String descjapjap_cgtagger = "";	
		if (descjapjap_cgtagger.length() > 0) {
			Label descLabeljapjap_cgtagger = new Label(editGroupjapjap_cgtagger, SWT.WRAP);
			descLabeljapjap_cgtagger.setText(descjapjap_cgtagger);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"jap.cgtagger"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setjapjap_cgtaggerenabled_widget(new BooleanOptionWidget(editGroupjapjap_cgtagger, SWT.NONE, new OptionData("Enabled", "p", "jap.cgtagger","enabled", "\n", defaultBool)));
		
		

		
		return editGroupjapjap_cgtagger;
	}



	private Composite gbCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupgb = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupgb.setLayout(layout);
	
	 	editGroupgb.setText("Grimp Body Creation");
	 	
		editGroupgb.setData("id", "gb");
		
		String descgb = "";	
		if (descgb.length() > 0) {
			Label descLabelgb = new Label(editGroupgb, SWT.WRAP);
			descLabelgb.setText(descgb);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"gb"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgbenabled_widget(new BooleanOptionWidget(editGroupgb, SWT.NONE, new OptionData("Enabled", "p", "gb","enabled", "\n", defaultBool)));
		
		

		
		return editGroupgb;
	}



	private Composite gbgb_a1Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupgbgb_a1 = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupgbgb_a1.setLayout(layout);
	
	 	editGroupgbgb_a1.setText("Grimp Pre-folding Aggregator");
	 	
		editGroupgbgb_a1.setData("id", "gbgb_a1");
		
		String descgbgb_a1 = "";	
		if (descgbgb_a1.length() > 0) {
			Label descLabelgbgb_a1 = new Label(editGroupgbgb_a1, SWT.WRAP);
			descLabelgbgb_a1.setText(descgbgb_a1);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"gb.a1"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgbgb_a1enabled_widget(new BooleanOptionWidget(editGroupgbgb_a1, SWT.NONE, new OptionData("Enabled", "p", "gb.a1","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"gb.a1"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgbgb_a1only_stack_locals_widget(new BooleanOptionWidget(editGroupgbgb_a1, SWT.NONE, new OptionData("Only Stack Locals", "p", "gb.a1","only-stack-locals", "\nAggregate only values stored in stack locals. ", defaultBool)));
		
		

		
		return editGroupgbgb_a1;
	}



	private Composite gbgb_cfCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupgbgb_cf = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupgbgb_cf.setLayout(layout);
	
	 	editGroupgbgb_cf.setText("Grimp Constructor Folder");
	 	
		editGroupgbgb_cf.setData("id", "gbgb_cf");
		
		String descgbgb_cf = "";	
		if (descgbgb_cf.length() > 0) {
			Label descLabelgbgb_cf = new Label(editGroupgbgb_cf, SWT.WRAP);
			descLabelgbgb_cf.setText(descgbgb_cf);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"gb.cf"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgbgb_cfenabled_widget(new BooleanOptionWidget(editGroupgbgb_cf, SWT.NONE, new OptionData("Enabled", "p", "gb.cf","enabled", "\n", defaultBool)));
		
		

		
		return editGroupgbgb_cf;
	}



	private Composite gbgb_a2Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupgbgb_a2 = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupgbgb_a2.setLayout(layout);
	
	 	editGroupgbgb_a2.setText("Grimp Post-folding Aggregator");
	 	
		editGroupgbgb_a2.setData("id", "gbgb_a2");
		
		String descgbgb_a2 = "";	
		if (descgbgb_a2.length() > 0) {
			Label descLabelgbgb_a2 = new Label(editGroupgbgb_a2, SWT.WRAP);
			descLabelgbgb_a2.setText(descgbgb_a2);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"gb.a2"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgbgb_a2enabled_widget(new BooleanOptionWidget(editGroupgbgb_a2, SWT.NONE, new OptionData("Enabled", "p", "gb.a2","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"gb.a2"+" "+"only-stack-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgbgb_a2only_stack_locals_widget(new BooleanOptionWidget(editGroupgbgb_a2, SWT.NONE, new OptionData("Only Stack Locals", "p", "gb.a2","only-stack-locals", "\nAggregate only values stored in stack locals. ", defaultBool)));
		
		

		
		return editGroupgbgb_a2;
	}



	private Composite gbgb_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupgbgb_ule = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupgbgb_ule.setLayout(layout);
	
	 	editGroupgbgb_ule.setText("Grimp Unused Local Eliminator");
	 	
		editGroupgbgb_ule.setData("id", "gbgb_ule");
		
		String descgbgb_ule = "Removes unused locals";	
		if (descgbgb_ule.length() > 0) {
			Label descLabelgbgb_ule = new Label(editGroupgbgb_ule, SWT.WRAP);
			descLabelgbgb_ule.setText(descgbgb_ule);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"gb.ule"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setgbgb_uleenabled_widget(new BooleanOptionWidget(editGroupgbgb_ule, SWT.NONE, new OptionData("Enabled", "p", "gb.ule","enabled", "\n", defaultBool)));
		
		

		
		return editGroupgbgb_ule;
	}



	private Composite gopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupgop = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupgop.setLayout(layout);
	
	 	editGroupgop.setText("Grimp Optimization");
	 	
		editGroupgop.setData("id", "gop");
		
		String descgop = "";	
		if (descgop.length() > 0) {
			Label descLabelgop = new Label(editGroupgop, SWT.WRAP);
			descLabelgop.setText(descgop);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"gop"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setgopenabled_widget(new BooleanOptionWidget(editGroupgop, SWT.NONE, new OptionData("Enabled", "p", "gop","enabled", "\n", defaultBool)));
		
		

		
		return editGroupgop;
	}



	private Composite bbCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupbb = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupbb.setLayout(layout);
	
	 	editGroupbb.setText("Baf Body Creation");
	 	
		editGroupbb.setData("id", "bb");
		
		String descbb = "";	
		if (descbb.length() > 0) {
			Label descLabelbb = new Label(editGroupbb, SWT.WRAP);
			descLabelbb.setText(descbb);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"bb"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbbenabled_widget(new BooleanOptionWidget(editGroupbb, SWT.NONE, new OptionData("Enabled", "p", "bb","enabled", "\n", defaultBool)));
		
		

		
		return editGroupbb;
	}



	private Composite bbbb_lsoCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupbbbb_lso = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupbbbb_lso.setLayout(layout);
	
	 	editGroupbbbb_lso.setText("Load Store Optimizer");
	 	
		editGroupbbbb_lso.setData("id", "bbbb_lso");
		
		String descbbbb_lso = "";	
		if (descbbbb_lso.length() > 0) {
			Label descLabelbbbb_lso = new Label(editGroupbbbb_lso, SWT.WRAP);
			descLabelbbbb_lso.setText(descbbbb_lso);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"bb.lso"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbbbb_lsoenabled_widget(new BooleanOptionWidget(editGroupbbbb_lso, SWT.NONE, new OptionData("Enabled", "p", "bb.lso","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"debug";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lsodebug_widget(new BooleanOptionWidget(editGroupbbbb_lso, SWT.NONE, new OptionData("Debug", "p", "bb.lso","debug", "\nProduces voluminous debugging output describing the progress of \nthe load store optimizer. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"inter";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lsointer_widget(new BooleanOptionWidget(editGroupbbbb_lso, SWT.NONE, new OptionData("Inter", "p", "bb.lso","inter", "\nEnables two simple inter-block optimizations which attempt to \nkeep some variables on the stack between blocks. Both are \nintended to catch if-like constructions where control flow \nbranches temporarily into two paths that converge at a later \npoint. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"sl";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbbbb_lsosl_widget(new BooleanOptionWidget(editGroupbbbb_lso, SWT.NONE, new OptionData("sl", "p", "bb.lso","sl", "\nEnables an optimization which attempts to eliminate store/load \npairs. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"sl2";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lsosl2_widget(new BooleanOptionWidget(editGroupbbbb_lso, SWT.NONE, new OptionData("sl2", "p", "bb.lso","sl2", "\nEnables an a second pass of the optimization which attempts to \neliminate store/load pairs. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"sll";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbbbb_lsosll_widget(new BooleanOptionWidget(editGroupbbbb_lso, SWT.NONE, new OptionData("sll", "p", "bb.lso","sll", "\nEnables an optimization which attempts to eliminate \nstore/load/load trios with some variant of dup. ", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lso"+" "+"sll2";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lsosll2_widget(new BooleanOptionWidget(editGroupbbbb_lso, SWT.NONE, new OptionData("sll2", "p", "bb.lso","sll2", "\nEnables an a second pass of the optimization which attempts to \neliminate store/load/load trios with some variant of dup. ", defaultBool)));
		
		

		
		return editGroupbbbb_lso;
	}



	private Composite bbbb_phoCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupbbbb_pho = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupbbbb_pho.setLayout(layout);
	
	 	editGroupbbbb_pho.setText("Peephole Optimizer");
	 	
		editGroupbbbb_pho.setData("id", "bbbb_pho");
		
		String descbbbb_pho = "";	
		if (descbbbb_pho.length() > 0) {
			Label descLabelbbbb_pho = new Label(editGroupbbbb_pho, SWT.WRAP);
			descLabelbbbb_pho.setText(descbbbb_pho);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"bb.pho"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbbbb_phoenabled_widget(new BooleanOptionWidget(editGroupbbbb_pho, SWT.NONE, new OptionData("Enabled", "p", "bb.pho","enabled", "\n", defaultBool)));
		
		

		
		return editGroupbbbb_pho;
	}



	private Composite bbbb_uleCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupbbbb_ule = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupbbbb_ule.setLayout(layout);
	
	 	editGroupbbbb_ule.setText("Unused Local Eliminator");
	 	
		editGroupbbbb_ule.setData("id", "bbbb_ule");
		
		String descbbbb_ule = "Removes unused locals";	
		if (descbbbb_ule.length() > 0) {
			Label descLabelbbbb_ule = new Label(editGroupbbbb_ule, SWT.WRAP);
			descLabelbbbb_ule.setText(descbbbb_ule);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"bb.ule"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbbbb_uleenabled_widget(new BooleanOptionWidget(editGroupbbbb_ule, SWT.NONE, new OptionData("Enabled", "p", "bb.ule","enabled", "\n", defaultBool)));
		
		

		
		return editGroupbbbb_ule;
	}



	private Composite bbbb_lpCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupbbbb_lp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupbbbb_lp.setLayout(layout);
	
	 	editGroupbbbb_lp.setText("Local Packer");
	 	
		editGroupbbbb_lp.setData("id", "bbbb_lp");
		
		String descbbbb_lp = "Minimizes number of locals";	
		if (descbbbb_lp.length() > 0) {
			Label descLabelbbbb_lp = new Label(editGroupbbbb_lp, SWT.WRAP);
			descLabelbbbb_lp.setText(descbbbb_lp);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"bb.lp"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		setbbbb_lpenabled_widget(new BooleanOptionWidget(editGroupbbbb_lp, SWT.NONE, new OptionData("Enabled", "p", "bb.lp","enabled", "\n", defaultBool)));
		
		
		
		defKey = "p"+" "+"bb.lp"+" "+"unsplit-original-locals";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbbbb_lpunsplit_original_locals_widget(new BooleanOptionWidget(editGroupbbbb_lp, SWT.NONE, new OptionData("Unsplit Original Locals", "p", "bb.lp","unsplit-original-locals", "\nUse the variable names in the original source as a guide when \ndetermining how to share local variables across non-interfering \nvariable usages. This recombines named locals which were split \nby the Local Splitter. SHOULD WE ENSURE THAT IF jb.ulp IS ALSO \nENABLED, THEN ITS unsplit-original-locals MATCHES THIS ONE? ", defaultBool)));
		
		

		
		return editGroupbbbb_lp;
	}



	private Composite bopCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupbop = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupbop.setLayout(layout);
	
	 	editGroupbop.setText("Baf Optimization");
	 	
		editGroupbop.setData("id", "bop");
		
		String descbop = "";	
		if (descbop.length() > 0) {
			Label descLabelbop = new Label(editGroupbop, SWT.WRAP);
			descLabelbop.setText(descbop);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"bop"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setbopenabled_widget(new BooleanOptionWidget(editGroupbop, SWT.NONE, new OptionData("Enabled", "p", "bop","enabled", "\n", defaultBool)));
		
		

		
		return editGroupbop;
	}



	private Composite tagCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGrouptag = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGrouptag.setLayout(layout);
	
	 	editGrouptag.setText("Tag");
	 	
		editGrouptag.setData("id", "tag");
		
		String desctag = "";	
		if (desctag.length() > 0) {
			Label descLabeltag = new Label(editGrouptag, SWT.WRAP);
			descLabeltag.setText(desctag);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"tag"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = true;
			
		}

		settagenabled_widget(new BooleanOptionWidget(editGrouptag, SWT.NONE, new OptionData("Enabled", "p", "tag","enabled", "\n", defaultBool)));
		
		

		
		return editGrouptag;
	}



	private Composite tagtag_lnCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGrouptagtag_ln = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGrouptagtag_ln.setLayout(layout);
	
	 	editGrouptagtag_ln.setText("Line Number Tag Aggregator");
	 	
		editGrouptagtag_ln.setData("id", "tagtag_ln");
		
		String desctagtag_ln = "";	
		if (desctagtag_ln.length() > 0) {
			Label descLabeltagtag_ln = new Label(editGrouptagtag_ln, SWT.WRAP);
			descLabeltagtag_ln.setText(desctagtag_ln);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"tag.ln"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		settagtag_lnenabled_widget(new BooleanOptionWidget(editGrouptagtag_ln, SWT.NONE, new OptionData("Enabled", "p", "tag.ln","enabled", "\n", defaultBool)));
		
		

		
		return editGrouptagtag_ln;
	}



	private Composite tagtag_anCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGrouptagtag_an = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGrouptagtag_an.setLayout(layout);
	
	 	editGrouptagtag_an.setText("Array Bounds and Null Pointer Check Tag Aggregator");
	 	
		editGrouptagtag_an.setData("id", "tagtag_an");
		
		String desctagtag_an = "";	
		if (desctagtag_an.length() > 0) {
			Label descLabeltagtag_an = new Label(editGrouptagtag_an, SWT.WRAP);
			descLabeltagtag_an.setText(desctagtag_an);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"tag.an"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		settagtag_anenabled_widget(new BooleanOptionWidget(editGrouptagtag_an, SWT.NONE, new OptionData("Enabled", "p", "tag.an","enabled", "\n", defaultBool)));
		
		

		
		return editGrouptagtag_an;
	}



	private Composite tagtag_depCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGrouptagtag_dep = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGrouptagtag_dep.setLayout(layout);
	
	 	editGrouptagtag_dep.setText("Dependence Tag Aggregator");
	 	
		editGrouptagtag_dep.setData("id", "tagtag_dep");
		
		String desctagtag_dep = "";	
		if (desctagtag_dep.length() > 0) {
			Label descLabeltagtag_dep = new Label(editGrouptagtag_dep, SWT.WRAP);
			descLabeltagtag_dep.setText(desctagtag_dep);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"tag.dep"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		settagtag_depenabled_widget(new BooleanOptionWidget(editGrouptagtag_dep, SWT.NONE, new OptionData("Enabled", "p", "tag.dep","enabled", "\n", defaultBool)));
		
		

		
		return editGrouptagtag_dep;
	}



	private Composite tagtag_fieldrwCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGrouptagtag_fieldrw = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGrouptagtag_fieldrw.setLayout(layout);
	
	 	editGrouptagtag_fieldrw.setText("Field Read/Write Tag Aggregator");
	 	
		editGrouptagtag_fieldrw.setData("id", "tagtag_fieldrw");
		
		String desctagtag_fieldrw = "";	
		if (desctagtag_fieldrw.length() > 0) {
			Label descLabeltagtag_fieldrw = new Label(editGrouptagtag_fieldrw, SWT.WRAP);
			descLabeltagtag_fieldrw.setText(desctagtag_fieldrw);
		}
		OptionData [] data;	
		

		
		defKey = "p"+" "+"tag.fieldrw"+" "+"enabled";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		settagtag_fieldrwenabled_widget(new BooleanOptionWidget(editGrouptagtag_fieldrw, SWT.NONE, new OptionData("Enabled", "p", "tag.fieldrw","enabled", "\n", defaultBool)));
		
		

		
		return editGrouptagtag_fieldrw;
	}



	private Composite Single_File_Mode_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupSingle_File_Mode_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupSingle_File_Mode_Options.setLayout(layout);
	
	 	editGroupSingle_File_Mode_Options.setText("Single File Mode Options");
	 	
		editGroupSingle_File_Mode_Options.setData("id", "Single_File_Mode_Options");
		
		String descSingle_File_Mode_Options = "";	
		if (descSingle_File_Mode_Options.length() > 0) {
			Label descLabelSingle_File_Mode_Options = new Label(editGroupSingle_File_Mode_Options, SWT.WRAP);
			descLabelSingle_File_Mode_Options.setText(descSingle_File_Mode_Options);
		}
		OptionData [] data;	
		


		defKey = ""+" "+""+" "+"process-path";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setSingle_File_Mode_Optionsprocess_path_widget(new ListOptionWidget(editGroupSingle_File_Mode_Options, SWT.NONE, new OptionData("Process Path",  "", "","process-path", "\nProcess all classes in PATH. All the classes found in PATH will \nbe loaded and transformed in single-file mode. ", defaultString)));
		

		
		return editGroupSingle_File_Mode_Options;
	}



	private Composite Application_Mode_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupApplication_Mode_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupApplication_Mode_Options.setLayout(layout);
	
	 	editGroupApplication_Mode_Options.setText("Application Mode Options");
	 	
		editGroupApplication_Mode_Options.setData("id", "Application_Mode_Options");
		
		String descApplication_Mode_Options = "";	
		if (descApplication_Mode_Options.length() > 0) {
			Label descLabelApplication_Mode_Options = new Label(editGroupApplication_Mode_Options, SWT.WRAP);
			descLabelApplication_Mode_Options.setText(descApplication_Mode_Options);
		}
		OptionData [] data;	
		


		defKey = ""+" "+""+" "+"i";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsinclude_widget(new ListOptionWidget(editGroupApplication_Mode_Options, SWT.NONE, new OptionData("Include Package",  "", "","i", "\nMarks the classfiles in PACKAGE (e.g. java.util.) as application \nclasses. This option can be used to transform library types \nwhich by default are not transformed by Soot.", defaultString)));
		

		defKey = ""+" "+""+" "+"x";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsexclude_widget(new ListOptionWidget(editGroupApplication_Mode_Options, SWT.NONE, new OptionData("Exclude Package",  "", "","x", "\nMarks classfiles in PACKAGE (e.g. java.) as context classes. \nJimple is not produced for context classes, but the SootClass, \nSootField and SootMethod signature objects are created.", defaultString)));
		

		defKey = ""+" "+""+" "+"dynamic-classes";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsdynamic_classes_widget(new ListOptionWidget(editGroupApplication_Mode_Options, SWT.NONE, new OptionData("Dynamic Classes",  "", "","dynamic-classes", "\nThis option marks CLASSES (separated by colons) as potentially \ndynamic classes. ", defaultString)));
		

		defKey = ""+" "+""+" "+"dynamic-path";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsdynamic_path_widget(new ListOptionWidget(editGroupApplication_Mode_Options, SWT.NONE, new OptionData("Dynamic Path",  "", "","dynamic-path", "\nMarks all class files in PATH as potentially dynamic classes. \nThis allows aggressive optimization of applications for which \nthe set of dynamic classes that can be loaded is known at \ncompile time.", defaultString)));
		

		defKey = ""+" "+""+" "+"dynamic-package";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		}
		else {
			
			defaultString = "";
			
		}

		setApplication_Mode_Optionsdynamic_package_widget(new ListOptionWidget(editGroupApplication_Mode_Options, SWT.NONE, new OptionData("Dynamic Package",  "", "","dynamic-package", "\nMarks all class files belonging to a package listed in PACKAGES \n(or one of its subpackages) as potentially dynamic classes.", defaultString)));
		

		
		return editGroupApplication_Mode_Options;
	}



	private Composite Input_Attribute_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupInput_Attribute_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupInput_Attribute_Options.setLayout(layout);
	
	 	editGroupInput_Attribute_Options.setText("Input Attribute Options");
	 	
		editGroupInput_Attribute_Options.setData("id", "Input_Attribute_Options");
		
		String descInput_Attribute_Options = "";	
		if (descInput_Attribute_Options.length() > 0) {
			Label descLabelInput_Attribute_Options = new Label(editGroupInput_Attribute_Options, SWT.WRAP);
			descLabelInput_Attribute_Options.setText(descInput_Attribute_Options);
		}
		OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"keep-line-number";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setInput_Attribute_Optionskeep_line_number_widget(new BooleanOptionWidget(editGroupInput_Attribute_Options, SWT.NONE, new OptionData("Keep Line Number", "", "","keep-line-number", "\nPreserves the line number tables of class files throughout the \ntransformations.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"keep-bytecode-offset";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setInput_Attribute_Optionskeep_offset_widget(new BooleanOptionWidget(editGroupInput_Attribute_Options, SWT.NONE, new OptionData("Keep Bytecode Offset", "", "","keep-bytecode-offset", "\nPreserves the bytecode offset tables of class files throughout \nthe transformations.", defaultBool)));
		
		

		
		return editGroupInput_Attribute_Options;
	}



	private Composite Annotation_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupAnnotation_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupAnnotation_Options.setLayout(layout);
	
	 	editGroupAnnotation_Options.setText("Annotation Options");
	 	
		editGroupAnnotation_Options.setData("id", "Annotation_Options");
		
		String descAnnotation_Options = "";	
		if (descAnnotation_Options.length() > 0) {
			Label descLabelAnnotation_Options = new Label(editGroupAnnotation_Options, SWT.WRAP);
			descLabelAnnotation_Options.setText(descAnnotation_Options);
		}
		OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"annot-nullpointer";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setAnnotation_Optionsannot_nullpointer_widget(new BooleanOptionWidget(editGroupAnnotation_Options, SWT.NONE, new OptionData("Null Pointer Annotation", "", "","annot-nullpointer", "\nThis option turns on annotations for Null Pointer. This creates \nannotations that can be added to class files and later used by \nthe JVM.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"annot-arraybounds";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setAnnotation_Optionsannot_arraybounds_widget(new BooleanOptionWidget(editGroupAnnotation_Options, SWT.NONE, new OptionData("Array Bounds Annotation", "", "","annot-arraybounds", "\nThis option turns on annotations for Array Bound Check. This \ncreates annotations that can be added to class files and later \nused by the JVM.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"annot-side-effect";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setAnnotation_Optionsannot_side_effect_widget(new BooleanOptionWidget(editGroupAnnotation_Options, SWT.NONE, new OptionData("Side effect annotation", "", "","annot-side-effect", "\nThis option turns on the generation of side-effect attributes.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"annot-fieldrw";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setAnnotation_Optionsannot_fieldrw_widget(new BooleanOptionWidget(editGroupAnnotation_Options, SWT.NONE, new OptionData("Field read/write annotation", "", "","annot-fieldrw", "\nThis option turns on the generation of field read/write \nattributes.", defaultBool)));
		
		

		
		return editGroupAnnotation_Options;
	}



	private Composite Miscellaneous_OptionsCreate(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
		
		Group editGroupMiscellaneous_Options = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupMiscellaneous_Options.setLayout(layout);
	
	 	editGroupMiscellaneous_Options.setText("Miscellaneous Options");
	 	
		editGroupMiscellaneous_Options.setData("id", "Miscellaneous_Options");
		
		String descMiscellaneous_Options = "";	
		if (descMiscellaneous_Options.length() > 0) {
			Label descLabelMiscellaneous_Options = new Label(editGroupMiscellaneous_Options, SWT.WRAP);
			descLabelMiscellaneous_Options.setText(descMiscellaneous_Options);
		}
		OptionData [] data;	
		

		
		defKey = ""+" "+""+" "+"time";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setMiscellaneous_Optionstime_widget(new BooleanOptionWidget(editGroupMiscellaneous_Options, SWT.NONE, new OptionData("Time", "", "","time", "\nPrint out time statistics about transformations.", defaultBool)));
		
		
		
		defKey = ""+" "+""+" "+"subtract-gc";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		}
		else {
			
			defaultBool = false;
			
		}

		setMiscellaneous_Optionssubtract_gc_widget(new BooleanOptionWidget(editGroupMiscellaneous_Options, SWT.NONE, new OptionData("Subtract Garbage Collection Time", "", "","subtract-gc", "\nAttempt to subtract garbage-collection time from the time stats.", defaultBool)));
		
		

		
		return editGroupMiscellaneous_Options;
	}




}

