@.QuickSort_vtable = global [0 x i8*] []
@.QS_vtable = global [4 x i8*] [i8* bitcast (i32 (i8*,i32)* @QS.Start to i8*), i8* bitcast (i32 (i8*,i32, i32)* @QS.Sort to i8*), i8* bitcast (i32 (i8*)* @QS.Print to i8*), i8* bitcast (i32 (i8*,i32)* @QS.Init to i8*)]


declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define void @throw_oob() {
	%_str = bitcast [15 x i8]* @_cOOB to i8*
	call i32 (i8*, ...) @printf(i8* %_str)
	call void @exit(i32 1)
	ret void
}

define i32 @main() {
	%_0 = call i8* @calloc(i32 1, i32 20)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [4 x i8*], [4 x i8*]* @.QS_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	%_3 = bitcast i8* %_0 to i8***
	%_4 = load i8**, i8*** %_3
	%_5 = getelementptr i8*, i8** %_4, i32 0
	%_6 = load i8*, i8** %_5
	%_7 = bitcast i8* %_6 to i32  (i8*,i32)*
	%_8 = call i32 %_7(i8* %_0, i32 10)
	call void (i32) @print_int(i32 %_8)
	ret i32 0
}

define i32 @QS.Start(i8* %this, i32 %.sz) {
	%sz = alloca i32
	store i32 %.sz, i32* %sz
	%aux01 = alloca i32

	%_0 = bitcast i8* %this to i8***
	%_1 = load i8**, i8*** %_0
	%_2 = getelementptr i8*, i8** %_1, i32 3
	%_3 = load i8*, i8** %_2
	%_4 = bitcast i8* %_3 to i32  (i8*,i32)*
	%_5 = load i32, i32* %sz
	%_6 = call i32 %_4(i8* %this, i32 %_5)
	store i32 %_6, i32* %aux01

	%_7 = bitcast i8* %this to i8***
	%_8 = load i8**, i8*** %_7
	%_9 = getelementptr i8*, i8** %_8, i32 2
	%_10 = load i8*, i8** %_9
	%_11 = bitcast i8* %_10 to i32  (i8*)*
	%_12 = call i32 %_11(i8* %this)
	store i32 %_12, i32* %aux01

	call void (i32) @print_int(i32 9999)
	%_14 = getelementptr i8, i8* %this, i32 16
	%_15 = bitcast i8* %_14 to i32*
	%_16 = load i32, i32* %_15
	%_13 = sub i32 %_16, 1
	store i32 %_13, i32* %aux01

	%_17 = bitcast i8* %this to i8***
	%_18 = load i8**, i8*** %_17
	%_19 = getelementptr i8*, i8** %_18, i32 1
	%_20 = load i8*, i8** %_19
	%_21 = bitcast i8* %_20 to i32  (i8*,i32, i32)*
	%_22 = load i32, i32* %aux01
	%_23 = call i32 %_21(i8* %this, i32 0, i32 %_22)
	store i32 %_23, i32* %aux01

	%_24 = bitcast i8* %this to i8***
	%_25 = load i8**, i8*** %_24
	%_26 = getelementptr i8*, i8** %_25, i32 2
	%_27 = load i8*, i8** %_26
	%_28 = bitcast i8* %_27 to i32  (i8*)*
	%_29 = call i32 %_28(i8* %this)
	store i32 %_29, i32* %aux01

	ret i32 0
}

define i32 @QS.Sort(i8* %this, i32 %.left, i32 %.right) {
	%left = alloca i32
	store i32 %.left, i32* %left
	%right = alloca i32
	store i32 %.right, i32* %right
	%v = alloca i32

	%i = alloca i32

	%j = alloca i32

	%nt = alloca i32

	%t = alloca i32

	%cont01 = alloca i1

	%cont02 = alloca i1

	%aux03 = alloca i32

	store i32 0, i32* %t

	%_1 = load i32, i32* %left
	%_2 = load i32, i32* %right
	%_0 = icmp slt i32 %_1, %_2
	br i1 %_0, label %if0, label %if1

if0:
	%_3 = getelementptr i8, i8* %this, i32 8
	%_4 = bitcast i8* %_3 to i32**
	%_5 = load i32*, i32** %_4
	%_6 = load i32, i32* %right
	%_7 = add i32 %_6, 1
	%_8 = getelementptr i32, i32* %_5, i32 %_7
	%_9 = load i32, i32* %_8

	store i32 %_9, i32* %v

	%_11 = load i32, i32* %left
	%_10 = sub i32 %_11, 1
	store i32 %_10, i32* %i

	%_12 = load i32, i32* %right
	store i32 %_12, i32* %j

	store i1 1, i1* %cont01

	br label %if3
if3:
	%_13 = load i1, i1* %cont01
	br i1 %_13, label %if5, label %if4
if5:
	store i1 1, i1* %cont02

	br label %if6
if6:
	%_14 = load i1, i1* %cont02
	br i1 %_14, label %if8, label %if7
if8:
	%_16 = load i32, i32* %i
	%_15 = add i32 %_16, 1
	store i32 %_15, i32* %i

	%_17 = getelementptr i8, i8* %this, i32 8
	%_18 = bitcast i8* %_17 to i32**
	%_19 = load i32*, i32** %_18
	%_20 = load i32, i32* %i
	%_21 = add i32 %_20, 1
	%_22 = getelementptr i32, i32* %_19, i32 %_21
	%_23 = load i32, i32* %_22

	store i32 %_23, i32* %aux03

	%_25 = load i32, i32* %aux03
	%_26 = load i32, i32* %v
	%_24 = icmp slt i32 %_25, %_26
	%_27 = xor i1 1, %_24
	br i1 %_27, label %if9, label %if10

if9:
	store i1 0, i1* %cont02

	br label %if11

if10:
	store i1 1, i1* %cont02

	br label %if11

if11:

	br label %if6
if7:

	store i1 1, i1* %cont02

	br label %if12
if12:
	%_28 = load i1, i1* %cont02
	br i1 %_28, label %if14, label %if13
if14:
	%_30 = load i32, i32* %j
	%_29 = sub i32 %_30, 1
	store i32 %_29, i32* %j

	%_31 = getelementptr i8, i8* %this, i32 8
	%_32 = bitcast i8* %_31 to i32**
	%_33 = load i32*, i32** %_32
	%_34 = load i32, i32* %j
	%_35 = add i32 %_34, 1
	%_36 = getelementptr i32, i32* %_33, i32 %_35
	%_37 = load i32, i32* %_36

	store i32 %_37, i32* %aux03

	%_39 = load i32, i32* %v
	%_40 = load i32, i32* %aux03
	%_38 = icmp slt i32 %_39, %_40
	%_41 = xor i1 1, %_38
	br i1 %_41, label %if15, label %if16

if15:
	store i1 0, i1* %cont02

	br label %if17

if16:
	store i1 1, i1* %cont02

	br label %if17

if17:

	br label %if12
if13:

	%_42 = getelementptr i8, i8* %this, i32 8
	%_43 = bitcast i8* %_42 to i32**
	%_44 = load i32*, i32** %_43
	%_45 = load i32, i32* %i
	%_46 = add i32 %_45, 1
	%_47 = getelementptr i32, i32* %_44, i32 %_46
	%_48 = load i32, i32* %_47

	store i32 %_48, i32* %t

	%_49 = getelementptr i8, i8* %this, i32 8
	%_50 = bitcast i8* %_49 to i32**
	%_51 = load i32*, i32** %_50
	%_52 = load i32, i32* %i
	%_53 = getelementptr i8, i8* %this, i32 8
	%_54 = bitcast i8* %_53 to i32**
	%_55 = load i32*, i32** %_54
	%_56 = load i32, i32* %j
	%_57 = add i32 %_56, 1
	%_58 = getelementptr i32, i32* %_55, i32 %_57
	%_59 = load i32, i32* %_58

	%_60 = add i32 %_52, 1
	%_61 = getelementptr i32, i32* %_51, i32 %_60
	store i32 %_59, i32* %_61
	%_63 = getelementptr i8, i8* %this, i32 8
	%_64 = bitcast i8* %_63 to i32**
	%_65 = load i32*, i32** %_64
	%_66 = load i32, i32* %j
	%_67 = load i32, i32* %t
	%_68 = add i32 %_66, 1
	%_69 = getelementptr i32, i32* %_65, i32 %_68
	store i32 %_67, i32* %_69
	%_72 = load i32, i32* %j
	%_74 = load i32, i32* %i
	%_73 = add i32 %_74, 1
	%_71 = icmp slt i32 %_72, %_73
	br i1 %_71, label %if18, label %if19

if18:
	store i1 0, i1* %cont01

	br label %if20

if19:
	store i1 1, i1* %cont01

	br label %if20

if20:

	br label %if3
if4:

	%_75 = getelementptr i8, i8* %this, i32 8
	%_76 = bitcast i8* %_75 to i32**
	%_77 = load i32*, i32** %_76
	%_78 = load i32, i32* %j
	%_79 = getelementptr i8, i8* %this, i32 8
	%_80 = bitcast i8* %_79 to i32**
	%_81 = load i32*, i32** %_80
	%_82 = load i32, i32* %i
	%_83 = add i32 %_82, 1
	%_84 = getelementptr i32, i32* %_81, i32 %_83
	%_85 = load i32, i32* %_84

	%_86 = add i32 %_78, 1
	%_87 = getelementptr i32, i32* %_77, i32 %_86
	store i32 %_85, i32* %_87
	%_89 = getelementptr i8, i8* %this, i32 8
	%_90 = bitcast i8* %_89 to i32**
	%_91 = load i32*, i32** %_90
	%_92 = load i32, i32* %i
	%_93 = getelementptr i8, i8* %this, i32 8
	%_94 = bitcast i8* %_93 to i32**
	%_95 = load i32*, i32** %_94
	%_96 = load i32, i32* %right
	%_97 = add i32 %_96, 1
	%_98 = getelementptr i32, i32* %_95, i32 %_97
	%_99 = load i32, i32* %_98

	%_100 = add i32 %_92, 1
	%_101 = getelementptr i32, i32* %_91, i32 %_100
	store i32 %_99, i32* %_101
	%_103 = getelementptr i8, i8* %this, i32 8
	%_104 = bitcast i8* %_103 to i32**
	%_105 = load i32*, i32** %_104
	%_106 = load i32, i32* %right
	%_107 = load i32, i32* %t
	%_108 = add i32 %_106, 1
	%_109 = getelementptr i32, i32* %_105, i32 %_108
	store i32 %_107, i32* %_109
	%_111 = bitcast i8* %this to i8***
	%_112 = load i8**, i8*** %_111
	%_113 = getelementptr i8*, i8** %_112, i32 1
	%_114 = load i8*, i8** %_113
	%_115 = bitcast i8* %_114 to i32  (i8*,i32, i32)*
	%_116 = load i32, i32* %left
	%_118 = load i32, i32* %i
	%_117 = sub i32 %_118, 1
	%_119 = call i32 %_115(i8* %this, i32 %_116, i32 %_117)
	store i32 %_119, i32* %nt

	%_120 = bitcast i8* %this to i8***
	%_121 = load i8**, i8*** %_120
	%_122 = getelementptr i8*, i8** %_121, i32 1
	%_123 = load i8*, i8** %_122
	%_124 = bitcast i8* %_123 to i32  (i8*,i32, i32)*
	%_126 = load i32, i32* %i
	%_125 = add i32 %_126, 1
	%_127 = load i32, i32* %right
	%_128 = call i32 %_124(i8* %this, i32 %_125, i32 %_127)
	store i32 %_128, i32* %nt

	br label %if2

if1:
	store i32 0, i32* %nt

	br label %if2

if2:

	ret i32 0
}

define i32 @QS.Print(i8* %this) {
	%j = alloca i32

	store i32 0, i32* %j

	br label %if0
if0:
	%_1 = load i32, i32* %j
	%_2 = getelementptr i8, i8* %this, i32 16
	%_3 = bitcast i8* %_2 to i32*
	%_4 = load i32, i32* %_3
	%_0 = icmp slt i32 %_1, %_4
	br i1 %_0, label %if2, label %if1
if2:
	%_5 = getelementptr i8, i8* %this, i32 8
	%_6 = bitcast i8* %_5 to i32**
	%_7 = load i32*, i32** %_6
	%_8 = load i32, i32* %j
	%_9 = add i32 %_8, 1
	%_10 = getelementptr i32, i32* %_7, i32 %_9
	%_11 = load i32, i32* %_10

	call void (i32) @print_int(i32 %_11)
	%_13 = load i32, i32* %j
	%_12 = add i32 %_13, 1
	store i32 %_12, i32* %j

	br label %if0
if1:

	ret i32 0
}

define i32 @QS.Init(i8* %this, i32 %.sz) {
	%sz = alloca i32
	store i32 %.sz, i32* %sz
	%_2 = load i32, i32* %sz
	%_0 = getelementptr i8, i8* %this, i32 16
	%_1 = bitcast i8* %_0 to i32*
	store i32 %_2, i32* %_1

	%_5 = load i32, i32* %sz
	%_6 = add i32 %_5, 1
	%_7 = call i8* @calloc(i32 4, i32 %_6)
	%_8 = bitcast i8* %_7 to i32*
	store i32 %_5, i32* %_8
	%_3 = getelementptr i8, i8* %this, i32 8
	%_4 = bitcast i8* %_3 to i32**
	store i32* %_8, i32** %_4

	%_9 = getelementptr i8, i8* %this, i32 8
	%_10 = bitcast i8* %_9 to i32**
	%_11 = load i32*, i32** %_10
	%_12 = add i32 0, 1
	%_13 = getelementptr i32, i32* %_11, i32 %_12
	store i32 20, i32* %_13
	%_15 = getelementptr i8, i8* %this, i32 8
	%_16 = bitcast i8* %_15 to i32**
	%_17 = load i32*, i32** %_16
	%_18 = add i32 1, 1
	%_19 = getelementptr i32, i32* %_17, i32 %_18
	store i32 7, i32* %_19
	%_21 = getelementptr i8, i8* %this, i32 8
	%_22 = bitcast i8* %_21 to i32**
	%_23 = load i32*, i32** %_22
	%_24 = add i32 2, 1
	%_25 = getelementptr i32, i32* %_23, i32 %_24
	store i32 12, i32* %_25
	%_27 = getelementptr i8, i8* %this, i32 8
	%_28 = bitcast i8* %_27 to i32**
	%_29 = load i32*, i32** %_28
	%_30 = add i32 3, 1
	%_31 = getelementptr i32, i32* %_29, i32 %_30
	store i32 18, i32* %_31
	%_33 = getelementptr i8, i8* %this, i32 8
	%_34 = bitcast i8* %_33 to i32**
	%_35 = load i32*, i32** %_34
	%_36 = add i32 4, 1
	%_37 = getelementptr i32, i32* %_35, i32 %_36
	store i32 2, i32* %_37
	%_39 = getelementptr i8, i8* %this, i32 8
	%_40 = bitcast i8* %_39 to i32**
	%_41 = load i32*, i32** %_40
	%_42 = add i32 5, 1
	%_43 = getelementptr i32, i32* %_41, i32 %_42
	store i32 11, i32* %_43
	%_45 = getelementptr i8, i8* %this, i32 8
	%_46 = bitcast i8* %_45 to i32**
	%_47 = load i32*, i32** %_46
	%_48 = add i32 6, 1
	%_49 = getelementptr i32, i32* %_47, i32 %_48
	store i32 6, i32* %_49
	%_51 = getelementptr i8, i8* %this, i32 8
	%_52 = bitcast i8* %_51 to i32**
	%_53 = load i32*, i32** %_52
	%_54 = add i32 7, 1
	%_55 = getelementptr i32, i32* %_53, i32 %_54
	store i32 9, i32* %_55
	%_57 = getelementptr i8, i8* %this, i32 8
	%_58 = bitcast i8* %_57 to i32**
	%_59 = load i32*, i32** %_58
	%_60 = add i32 8, 1
	%_61 = getelementptr i32, i32* %_59, i32 %_60
	store i32 19, i32* %_61
	%_63 = getelementptr i8, i8* %this, i32 8
	%_64 = bitcast i8* %_63 to i32**
	%_65 = load i32*, i32** %_64
	%_66 = add i32 9, 1
	%_67 = getelementptr i32, i32* %_65, i32 %_66
	store i32 5, i32* %_67
	ret i32 0
}
