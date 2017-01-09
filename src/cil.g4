grammar cil;

compileUnit : topLevelDef* EOF;

// At the top level, we can have classes and assemblies
topLevelDef :
	(assemblyDef | moduleDef | classDef | dataDef)+;

// ----------------------
// Assembly Definition
// ----------------------

assemblyDef :
	'.assembly' ('extern')? ID '{' assemblyData* '}';

assemblyData :
	customAttributeDef |
	publicKeyDef |
	assemblyVersionDef |
	hashAlgorithmDef |
	permissionDef;

publicKeyDef :
	'.publickeytoken' '=' HEXARRAY;

assemblyVersionDef :
	'.ver' VERSION;

hashAlgorithmDef :
	'.hash algorithm' HEXSTRING;

permissionDef :
	'.permissionset' ('reqmin') '=' '{'
		(typeRef '=' propertyValueList ',')*
		(typeRef '=' propertyValueList)
	'}';

propertyValueList :
	'{'
		(propertyValuePair ',')*
		propertyValuePair
	'}';

propertyValuePair :
	'property' primOrTypeRef QUOTEDID '=' primOrTypeRef argList;

// ----------------------
// Module Definition
// ----------------------

moduleDef :
	'.module' ID
	(
		customAttributeDef?
		('.imagebase' HEXSTRING)?
		('.file alignment' HEXSTRING)
		('.stackreserve' HEXSTRING)
		('.subsystem' HEXSTRING)
		('.corflags' HEXSTRING)
	)*;

// -----------------------
// Very Basic Stuff
// -----------------------

accessModifier : 'private' | 'public';

primType : 'void'
	| 'char'
	| 'string'
	| 'object'
	| 'bool'

	| (('unsigned' | 'native')* 'int')
	| ('unsigned'? 'int8')
	| ('unsigned'? 'int16')
	| ('unsigned'? 'int32')
	| ('unsigned'? 'int64')
	
	| 'float32'
	| 'float64'
	
	| 'uint'
	| 'uint8'
	| 'uint16'
	| 'uint32'
	| 'uint64'
	
	| 'decimal'
	;
	
methodName : (ID | QUOTEDID);
assemblyName : (ID | QUOTEDID);
className : (ID | QUOTEDID);
fieldName : (ID | QUOTEDID);
eventName : (ID | QUOTEDID);
propertyName : (ID | QUOTEDID);

returnType : primOrTypeRef;

// -----------------------
// Class Definition
// -----------------------

classFlag : 'auto' |
	'ansi' |
	'sealed' |
	'beforefieldinit' |
	'nested' |
	'sequential' |
	'explicit';

classExtension : 'extends' typeRef;
classImplements : 'implements' (typeRef ',')* typeRef;

classDef :
	'.class'
		(
			accessModifier |
			classFlag
		)*
	className
	classExtension?
	classImplements?
	'{'
		(
			customAttributeDef |
			fieldDef |
			methodDef |
			classDef |
			eventDef |
			propertyDef |
			classDirective
		)*
	'}';

classDirective :
	(
		('.pack' NUMBER) |
		('.size' NUMBER)
	);

// -----------------------
// Field Definition
// -----------------------

fieldDef : '.field'
	(
		accessModifier |
		'static' |
		'family' |
		'assembly'
	)*
	primOrTypeRef
	fieldName
	fieldInitialization?;

fieldInitialization : 'at'
	DATAOFFSET;

// -----------------------
// Method Definition
// -----------------------

methodFlag : 'static' |
	'virtual' |
	'instance' |
	'hidebysig' |
	'specialname' |
	'rtspecialname' |
	'newslot' |
	'final';

methodManagementFlag : ('cil' 'managed')
	| ('runtime' 'managed');

methodDef : '.method'
	(
		accessModifier |
		methodFlag
	)*
	returnType
	methodName
	parameterList
	methodManagementFlag
	'{'
		(
			customAttributeDef |
			methodDirective
		)*
		instruction*
	'}';

parameterType : primOrTypeRef;
parameterName : ID | QUOTEDID;
parameter : parameterType parameterName;

parameterList : '('
	(
		(parameter ',')*
		(parameter)
	)?
	')';

methodDirective :
	(
		('.maxstack' NUMBER) |
		('.locals' 'init' localInitList) |
		('.entrypoint') |
		('.param' '[' NUMBER ']')
	);

localInitList :
	'('
		(localInitEntry ',')*
		(localInitEntry)
	')';

localInitEntry : ('[' NUMBER ']')? primOrTypeRef (ID | QUOTEDID);

instruction :
	(
		bytecodeOffset ':' il_inst
	)
	| tryCatchBlock;
bytecodeOffset : BYTECODEOFFSET;

// missing: jmp, calli, switch, cpobj, refanyval, ckfinite, cpblk, endfilter,
// ldarg, ldarga, starg, ldloc, ldloca, stloc
il_inst :
	(
		il_inst_nop |
		il_inst_break |
		
		il_inst_ldfld |
		il_inst_ldflda |
		il_inst_ldsfld |
		il_inst_ldarg |
		il_inst_ldargs |
		il_inst_ldarga |
		il_inst_ldstr |
		il_inst_ldnull |
		il_inst_ldftn |
		il_inst_ldvirtftn |
		il_inst_ldtoken |
		il_inst_ldobj |

		il_inst_stfld |
		il_inst_starg |
		il_inst_stargs |

		il_inst_ldelema |
		il_inst_ldelemi1 |
		il_inst_ldelemu1 |
		il_inst_ldelemi2 |
		il_inst_ldelemu2 |
		il_inst_ldelemi4 |
		il_inst_ldelemu4 |
		il_inst_ldelemi8 |
		il_inst_ldelemi |
		il_inst_ldelemr4 |
		il_inst_ldelemr8 |
		il_inst_ldelemref |
		il_inst_ldelem |

		il_inst_ldinti1 |
		il_inst_ldintu1 |
		il_inst_ldinti2 |
		il_inst_ldintu2 |
		il_inst_ldinti4 |
		il_inst_ldintu4 |
		il_inst_ldinti8 |
		il_inst_ldinti |
		il_inst_ldintr4 |
		il_inst_ldintr8 |
		il_inst_ldintref |
		
		il_inst_stelemi |
		il_inst_stelemi1 |
		il_inst_stelemi2 |
		il_inst_stelemi4 |
		il_inst_stelemi8 |
		il_inst_stelemr4 |
		il_inst_stelemr8 |
		il_inst_stelemref |
		il_inst_stelem |

		il_inst_stindref |
		il_inst_stindi |
		il_inst_stindi1 |
		il_inst_stindi2 |
		il_inst_stindi4 |
		il_inst_stindi8 |
		il_inst_stindr4 |
		il_inst_stindr8 |
		
		il_inst_add |
		il_inst_addovf |
		il_inst_addovfun |
		
		il_inst_sub |
		il_inst_subovf |
		il_inst_subovfun |
		
		il_inst_mul |
		il_inst_mulovf |
		il_inst_mulovfun |
		
		il_inst_div |
		il_inst_divun |
		il_inst_rem |
		il_inst_remun |

		il_inst_and |
		il_inst_or |
		il_inst_xor |
		il_inst_shl |
		il_inst_shr |
		il_inst_shrun |
		il_inst_neg |
		il_inst_not |
		
		il_inst_stsfld |
		il_inst_stobj |
		
		il_inst_box |
		il_inst_unbox |
		il_inst_unboxany |
		il_inst_newobj |
		il_inst_initobj |
		il_inst_newarr |
		il_inst_localloc |
		il_inst_call |
		il_inst_callvirt |
		il_inst_ret |
		il_inst_throw |
		il_inst_rethrow |
		
		il_inst_brs |
		il_inst_brfalses |
		il_inst_brtrues |
		il_inst_beqs |
		il_inst_bges |
		il_inst_bgts |
		il_inst_bles |
		il_inst_blts |
		il_inst_bneuns |
		il_inst_bgeuns |
		il_inst_bgtuns |
		il_inst_bleuns |
		il_inst_bltuns |
		il_inst_br |
		il_inst_brfalse |
		il_inst_brtrue |
		il_inst_beq |
		il_inst_bge |
		il_inst_bgt |
		il_inst_ble |
		il_inst_blt |
		il_inst_bneun |
		il_inst_bgeun |
		il_inst_bgtun |
		il_inst_bleun |
		il_inst_bltun |
		
		il_inst_stloc |
		il_inst_stlocs |
		il_inst_ldlen |
		il_inst_ldloc |
		il_inst_ldlocs |
		il_inst_ldlocas |
		
		il_inst_ldc_i4_m1 |
		il_inst_ldc_i4_0 |
		il_inst_ldc_i4_1 |
		il_inst_ldc_i4_2 |
		il_inst_ldc_i4_3 |
		il_inst_ldc_i4_4 |
		il_inst_ldc_i4_5 |
		il_inst_ldc_i4_6 |
		il_inst_ldc_i4_7 |
		il_inst_ldc_i4_8 |
		il_inst_ldc_i4_s |
		il_inst_ldc_i4 |
		il_inst_ldc_i8 |
		il_inst_ldc_r4 |
		il_inst_ldc_r8 |
		
		il_inst_endfinally |
		il_inst_leave |
		il_inst_leaves |
		
		il_inst_pop |
		il_inst_dup |
		
		il_inst_castclass |
		il_inst_convi |
		il_inst_convi1 |
		il_inst_convi2 |
		il_inst_convi4 |
		il_inst_convi8 |
		il_inst_convr4 |
		il_inst_convr8 |
		il_inst_convrun |
		il_inst_convu |
		il_inst_convu1 |
		il_inst_convu2 |
		il_inst_convu4 |
		il_inst_convu8 |
		
		il_inst_conv_ovfi1 |
		il_inst_conv_ovfi2 |
		il_inst_conv_ovfi4 |
		il_inst_conv_ovfi8 |
		il_inst_conv_ovfu1 |
		il_inst_conv_ovfu2 |
		il_inst_conv_ovfu4 |
		il_inst_conv_ovfu8 |
		il_inst_conv_ovfi |
		il_inst_conv_ovfu |

		il_inst_conv_ovfi1un |
		il_inst_conv_ovfi2un |
		il_inst_conv_ovfi4un |
		il_inst_conv_ovfi8un |
		il_inst_conv_ovfu1un |
		il_inst_conv_ovfu2un |
		il_inst_conv_ovfu4un |
		il_inst_conv_ovfu8un |
		il_inst_conv_ovfiun |
		il_inst_conv_ovfuun |
		
		il_inst_ceq |
		il_inst_cgt |
		il_inst_cgtun |
		il_inst_clt |
		il_inst_cltun |
		
		il_inst_isinst |
		il_inst_mkrefany |
		il_inst_arglist |
		il_inst_sizeof |
		il_inst_refanytype
	);

tryCatchBlock : '.try'
	'{'
		tryBlock
	'}'
	(
		(finallyDef | catchDef)
		'{'
			handlerBlock
		'}'
	)+;
finallyDef : 'finally';
catchDef : 'catch' typeRef;
tryBlock : instruction*;
handlerBlock : instruction*;
	
// -----------------------
// Event Definition
// -----------------------

eventDef : '.event' 
	typeRef
	eventName
	'{'
		(
			eventAddon |
			eventRemoveon
		)*
	'}';

eventAddon : '.addon'
	methodRef;

eventRemoveon : '.removeon'
	methodRef;

// -----------------------
// Property Definition
// -----------------------

propertyModifier : 'instance';

propertyDef : '.property'
	propertyModifier
	returnType
	propertyName
	argList
	'{'
		(
			propertyGetter |
			propertySetter
		)*
	'}';

propertyGetter : '.get'
	propertyModifier
	methodRef;

propertySetter : '.set'
	propertyModifier
	methodRef;

// -----------------------
// IL Instruction Set
// -----------------------

il_inst_nop : 'nop';
il_inst_break : 'break';

il_inst_ldfld : 'ldfld'
	staticFieldRef;

il_inst_ldflda : 'ldflda'
	staticFieldRef;

il_inst_ldsfld : 'ldsfld'
	staticFieldRef;

il_inst_ldarg : LDARG_NUMBER;

il_inst_ldargs : 'ldarg.s';

il_inst_ldarga : 'ldarga.s';

il_inst_ldstr : 'ldstr'
	STRING;

il_inst_ldnull : 'ldnull';

il_inst_ldftn : 'ldftn'
	methodRef;

il_inst_ldvirtftn : 'ldvirtftn'
	methodRef;

il_inst_ldtoken : 'ldtoken'
	('field' staticFieldRef) | typeRef;

il_inst_ldobj : 'ldobj'
	typeRef;

il_inst_stfld : 'stfld'
	staticFieldRef;

il_inst_starg : STARG_NUMBER;

il_inst_stargs : 'starg.s';

il_inst_ldelema : 'ldelem.a';
il_inst_ldelemi1 : 'ldelem.i1';
il_inst_ldelemu1 : 'ldelem.u1';
il_inst_ldelemi2 : 'ldelem.i2';
il_inst_ldelemu2 : 'ldelem.u2';
il_inst_ldelemi4 : 'ldelem.i4';
il_inst_ldelemu4 : 'ldelem.u4';
il_inst_ldelemi8 : 'ldelem.i8';
il_inst_ldelemi : 'ldelem.i';
il_inst_ldelemr4 : 'ldelem.r4';
il_inst_ldelemr8 : 'ldelem.r8';
il_inst_ldelemref : 'ldelem.ref';
il_inst_ldelem : 'ldelem';

il_inst_ldinti1 : 'ldint.i1';
il_inst_ldintu1 : 'ldint.u1';
il_inst_ldinti2 : 'ldint.i2';
il_inst_ldintu2 : 'ldint.u2';
il_inst_ldinti4 : 'ldint.i4';
il_inst_ldintu4 : 'ldint.u4';
il_inst_ldinti8 : 'ldint.i8';
il_inst_ldinti : 'ldint.i';
il_inst_ldintr4 : 'ldint.r4';
il_inst_ldintr8 : 'ldint.r8';
il_inst_ldintref : 'ldint.ref';

il_inst_stelemi : 'stelem.i';
il_inst_stelemi1 : 'stelem.i1';
il_inst_stelemi2 : 'stelem.i2';
il_inst_stelemi4 : 'stelem.i4';
il_inst_stelemi8 : 'stelem.i8';
il_inst_stelemr4 : 'stelem.r4';
il_inst_stelemr8 : 'stelem.r8';
il_inst_stelemref : 'stelem.ref';
il_inst_stelem : 'stelem';

il_inst_stindref : 'stind.ref';
il_inst_stindi : 'stind.i';
il_inst_stindi1 : 'stind.i1';
il_inst_stindi2 : 'stind.i2';
il_inst_stindi4 : 'stind.i4';
il_inst_stindi8 : 'stind.i8';
il_inst_stindr4 : 'stind.r4';
il_inst_stindr8 : 'stind.r8';

il_inst_add : 'add';
il_inst_addovf : 'add.ovf';
il_inst_addovfun : 'add.ovf.un';

il_inst_sub : 'sub';
il_inst_subovf : 'sub.ovf';
il_inst_subovfun : 'sub.ovf.un';

il_inst_mul : 'mul';
il_inst_mulovf : 'mul.ovf';
il_inst_mulovfun : 'mul.ovf.un';

il_inst_div : 'div';
il_inst_divun : 'div.un';
il_inst_rem : 'rem';
il_inst_remun : 'rem.un';

il_inst_and : 'and';
il_inst_or : 'or';
il_inst_xor : 'xor';
il_inst_shl : 'shl';
il_inst_shr : 'shr';
il_inst_shrun : 'shr.un';
il_inst_neg : 'neg';
il_inst_not : 'not';

il_inst_stsfld : 'stsfld'
	staticFieldRef;

il_inst_stobj : 'stobj'
	typeRef;

il_inst_box : 'box'
	typeRef;
il_inst_unbox : 'unbox'
	typeRef;
il_inst_unboxany : 'unbox.any'
	typeRef;

invokeFlags : 'instance';

il_inst_newobj : 'newobj' invokeFlags*
	methodRef;
il_inst_initobj : 'initobj'
	typeRef;
il_inst_newarr : 'newarr' typeRef;
il_inst_localloc : 'localloc';

il_inst_call : 'call' invokeFlags*
	methodRef;
il_inst_callvirt : 'callvirt' invokeFlags*
	methodRef;

il_inst_ret : 'ret';

il_inst_throw : 'throw';
il_inst_rethrow : 'rethrow';

il_inst_brs : 'br.s' BYTECODEOFFSET;
il_inst_brfalses : 'brfalse.s' BYTECODEOFFSET;
il_inst_brtrues : 'brtrue.s' BYTECODEOFFSET;
il_inst_beqs : 'beq.s' BYTECODEOFFSET;
il_inst_bges : 'bge.s' BYTECODEOFFSET;
il_inst_bgts : 'bgt.s' BYTECODEOFFSET;
il_inst_bles : 'ble.s' BYTECODEOFFSET;
il_inst_blts : 'blt.s' BYTECODEOFFSET;
il_inst_bneuns : 'bne.un.s' BYTECODEOFFSET;
il_inst_bgeuns : 'bge.un.s' BYTECODEOFFSET;
il_inst_bgtuns : 'bgt.un.s' BYTECODEOFFSET;
il_inst_bleuns : 'ble.un.s' BYTECODEOFFSET;
il_inst_bltuns : 'blt.un.s' BYTECODEOFFSET;
il_inst_br : 'br' BYTECODEOFFSET;
il_inst_brfalse : 'brfalse' BYTECODEOFFSET;
il_inst_brtrue : 'brtrue' BYTECODEOFFSET;
il_inst_beq : 'beq' BYTECODEOFFSET;
il_inst_bge : 'bge' BYTECODEOFFSET;
il_inst_bgt : 'bgt' BYTECODEOFFSET;
il_inst_ble : 'ble' BYTECODEOFFSET;
il_inst_blt : 'blt' BYTECODEOFFSET;
il_inst_bneun : 'bne.un' BYTECODEOFFSET;
il_inst_bgeun : 'bge.un' BYTECODEOFFSET;
il_inst_bgtun : 'bgt.un' BYTECODEOFFSET;
il_inst_bleun : 'ble.un' BYTECODEOFFSET;
il_inst_bltun : 'blt.un' BYTECODEOFFSET;

il_inst_stloc : STLOC_NUMBER;
il_inst_stlocs : 'stloc.s' ID;
il_inst_ldlen : 'ldlen';
il_inst_ldloc : LDLOC_NUMBER;
il_inst_ldlocs : 'ldloc.s' ID;
il_inst_ldlocas : 'ldloca.s' ID;

il_inst_ldc_i4_m1 : 'ldc.i4.m1';
il_inst_ldc_i4_0 : 'ldc.i4.0';
il_inst_ldc_i4_1 : 'ldc.i4.1';
il_inst_ldc_i4_2 : 'ldc.i4.2';
il_inst_ldc_i4_3 : 'ldc.i4.3';
il_inst_ldc_i4_4 : 'ldc.i4.4';
il_inst_ldc_i4_5 : 'ldc.i4.5';
il_inst_ldc_i4_6 : 'ldc.i4.6';
il_inst_ldc_i4_7 : 'ldc.i4.7';
il_inst_ldc_i4_8 : 'ldc.i4.8';
il_inst_ldc_i4_s : 'ldc.i4.s' (NUMBER | HEXSTRING);
il_inst_ldc_i4 : 'ldc.i4' (NUMBER | HEXSTRING);
il_inst_ldc_i8 : 'ldc.i8' (NUMBER | HEXSTRING);
il_inst_ldc_r4 : 'ldc.r4' (NUMBER | HEXSTRING);
il_inst_ldc_r8 : 'ldc.r8' (NUMBER | HEXSTRING);

il_inst_endfinally : 'endfinally';
il_inst_leave : 'leave' BYTECODEOFFSET;
il_inst_leaves : 'leave.s' BYTECODEOFFSET;

il_inst_pop : 'pop';
il_inst_dup : 'dup';

il_inst_castclass : 'castclass' typeRef;
il_inst_convi : 'conv.i';
il_inst_convi1 : 'conv.i1';
il_inst_convi2 : 'conv.i2';
il_inst_convi4 : 'conv.i4';
il_inst_convi8 : 'conv.i8';
il_inst_convr4 : 'conv.r4';
il_inst_convr8 : 'conv.r8';
il_inst_convrun : 'conv.r.un';
il_inst_convu : 'conv.u';
il_inst_convu1 : 'conv.u1';
il_inst_convu2 : 'conv.u2';
il_inst_convu4 : 'conv.u4';
il_inst_convu8 : 'conv.u8';

il_inst_conv_ovfi1 : 'conv.ovf.i1';
il_inst_conv_ovfi2 : 'conv.ovf.i2';
il_inst_conv_ovfi4 : 'conv.ovf.i4';
il_inst_conv_ovfi8 : 'conv.ovf.i8';
il_inst_conv_ovfu1 : 'conv.ovf.u1';
il_inst_conv_ovfu2 : 'conv.ovf.u2';
il_inst_conv_ovfu4 : 'conv.ovf.u4';
il_inst_conv_ovfu8 : 'conv.ovf.u8';
il_inst_conv_ovfi : 'conv.ovf.i';
il_inst_conv_ovfu : 'conv.ovf.u';

il_inst_conv_ovfi1un : 'conv.ovf.i1.un';
il_inst_conv_ovfi2un : 'conv.ovf.i2.un';
il_inst_conv_ovfi4un : 'conv.ovf.i4.un';
il_inst_conv_ovfi8un : 'conv.ovf.i8.un';
il_inst_conv_ovfu1un : 'conv.ovf.u1.un';
il_inst_conv_ovfu2un : 'conv.ovf.u2.un';
il_inst_conv_ovfu4un : 'conv.ovf.u4.un';
il_inst_conv_ovfu8un : 'conv.ovf.u8.un';
il_inst_conv_ovfiun : 'conv.ovf.i.un';
il_inst_conv_ovfuun : 'conv.ovf.u.un';

il_inst_ceq : 'ceq';
il_inst_cgt : 'cgt';
il_inst_cgtun : 'cgt.un';
il_inst_clt : 'clt';
il_inst_cltun : 'clt.un';

il_inst_isinst : 'isinst'
	typeRef;

il_inst_mkrefany : 'mkrefany'
	typeRef;

il_inst_arglist : 'arglist';

il_inst_sizeof : 'sizeof'
	typeRef;

il_inst_refanytype : 'refanytype';

// -----------------------
// Attribute Definitions
// -----------------------

customAttributeDef :
	'.custom' 'instance' methodRef '=' HEXARRAY;

// -----------------------
// Data Definitions
// -----------------------

dataOffset : DATAOFFSET;

dataDeclType : primOrTypeRef;

dataContents : HEXARRAY;

dataDef : '.data'
	'cil'
	dataOffset
	'='
	dataDeclType
	dataContents?;

// -----------------------
// References
// -----------------------

genericRef : ('!' | '!!') NUMBER '&'?;
genericsList : '`' NUMBER '<' (primOrTypeRef ',')* primOrTypeRef '>';
methodGenerics : '<' (primOrTypeRef ',')* primOrTypeRef '>';

arrayLength : NUMBER;
arrayType : (primType | typeRef | genericRef) (('[]') | ('[' arrayLength ']'))*;
pointerType : (primType | typeRef | genericRef) ('*')*;
primOrTypeRef : primType | typeRef | genericRef | arrayType | pointerType;

typeFlags : 'class' | 'valuetype';
typeRef : typeFlags? ('[' assemblyName ']')? className (genericsList)? ('[]')*;

argList : '(' ((primOrTypeRef ',')* primOrTypeRef)? ')';

methodRefFlags : 'class' | 'instance';
methodRef : methodRefFlags? returnType typeRef '::' methodName methodGenerics? argList;

staticFieldRef : returnType typeRef '::' fieldName;

// -----------------------
// Global Definitions
// -----------------------

LDARG_NUMBER : 'ldarg.' [0-9]+;
STARG_NUMBER : 'starg.' [0-9]+;
STLOC_NUMBER : 'stloc.' [0-9]+;
LDLOC_NUMBER : 'ldloc.' [0-9]+;

fragment ESCAPED_QUOTE : '\\"';
STRING : '"' (~[\r\n"] | '""' | ESCAPED_QUOTE)* '"';

NUMBER: [0-9.]+;
BYTECODEOFFSET: 'IL_' [0-9a-fA-F]+;
DATAOFFSET: 'I_' [0-9a-fA-F]+;

COMMENT : '//' ~[\r\n]* -> skip;

HEXARRAY : '(' [a-fA-F0-9 \t\r\n]+ ')';
HEXSTRING : '0x'[a-fA-F0-9]*;

QUOTEDID : ('\'' [=a-zA-Z.0-9<>$_/{}\-]+ '\'/')* '\'' [=a-zA-Z.0-9<>$_/{}\-]+ '\'';
ID  :   [a-zA-Z.0-9$_/]+;

WS : [ \t\r\n]+ -> skip ;

VERSION : [0-9]':'[0-9]':'[0-9]':'[0-9];
