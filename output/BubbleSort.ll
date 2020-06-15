@.BubbleSort_vtable = global [0 x i8*] []
@.BBS_vtable = global [4 x i8*] [i8* bitcast (i32 (i8*,i32)* @BBS.Start to i8*), i8* bitcast (i32 (i8*)* @BBS.Sort to i8*), i8* bitcast (i32 (i8*)* @BBS.Print to i8*), i8* bitcast (i32 (i8*,i32)* @BBS.Init to i8*)]


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
	%_2 = getelementptr [4 x i8*], [4 x i8*]* @.BBS_vtable, i32 0, i32 0
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

define i32 @BBS.Start(i8* %this, i32 %.sz) {
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

	call void (i32) @print_int(i32 99999)
	%_13 = bitcast i8* %this to i8***
	%_14 = load i8**, i8*** %_13
	%_15 = getelementptr i8*, i8** %_14, i32 1
	%_16 = load i8*, i8** %_15
	%_17 = bitcast i8* %_16 to i32  (i8*)*
	%_18 = call i32 %_17(i8* %this)
	store i32 %_18, i32* %aux01

	%_19 = bitcast i8* %this to i8***
	%_20 = load i8**, i8*** %_19
	%_21 = getelementptr i8*, i8** %_20, i32 2
	%_22 = load i8*, i8** %_21
	%_23 = bitcast i8* %_22 to i32  (i8*)*
	%_24 = call i32 %_23(i8* %this)
	store i32 %_24, i32* %aux01

	ret i32 0
}

define i32 @BBS.Sort(i8* %this) {
	%nt = alloca i32

	%i = alloca i32

	%aux02 = alloca i32

	%aux04 = alloca i32

	%aux05 = alloca i32

	%aux06 = alloca i32

	%aux07 = alloca i32

	%j = alloca i32

	%t = alloca i32

	%_1 = getelementptr i8, i8* %this, i32 16
	%_2 = bitcast i8* %_1 to i32*
	%_3 = load i32, i32* %_2
	%_0 = sub i32 %_3, 1
	store i32 %_0, i32* %i

	%_4 = sub i32 0, 1
	store i32 %_4, i32* %aux02

	br label %if0
if0:
	%_6 = load i32, i32* %aux02
	%_7 = load i32, i32* %i
	%_5 = icmp slt i32 %_6, %_7
	br i1 %_5, label %if2, label %if1
if2:
	store i32 1, i32* %j

	br label %if3
if3:
	%_9 = load i32, i32* %j
	%_11 = load i32, i32* %i
	%_10 = add i32 %_11, 1
	%_8 = icmp slt i32 %_9, %_10
	br i1 %_8, label %if5, label %if4
if5:
	%_13 = load i32, i32* %j
	%_12 = sub i32 %_13, 1
	store i32 %_12, i32* %aux07

	%_14 = getelementptr i8, i8* %this, i32 8
	%_15 = bitcast i8* %_14 to i32**
	%_16 = load i32*, i32** %_15
	%_17 = load i32, i32* %aux07
	%_18 = add i32 %_17, 1
	%_19 = getelementptr i32, i32* %_16, i32 %_18
	%_20 = load i32, i32* %_19

	store i32 %_20, i32* %aux04

	%_21 = getelementptr i8, i8* %this, i32 8
	%_22 = bitcast i8* %_21 to i32**
	%_23 = load i32*, i32** %_22
	%_24 = load i32, i32* %j
	%_25 = add i32 %_24, 1
	%_26 = getelementptr i32, i32* %_23, i32 %_25
	%_27 = load i32, i32* %_26

	store i32 %_27, i32* %aux05

	%_29 = load i32, i32* %aux05
	%_30 = load i32, i32* %aux04
	%_28 = icmp slt i32 %_29, %_30
	br i1 %_28, label %if6, label %if7

if6:
	%_32 = load i32, i32* %j
	%_31 = sub i32 %_32, 1
	store i32 %_31, i32* %aux06

	%_33 = getelementptr i8, i8* %this, i32 8
	%_34 = bitcast i8* %_33 to i32**
	%_35 = load i32*, i32** %_34
	%_36 = load i32, i32* %aux06
	%_37 = add i32 %_36, 1
	%_38 = getelementptr i32, i32* %_35, i32 %_37
	%_39 = load i32, i32* %_38

	store i32 %_39, i32* %t

	%_40 = getelementptr i8, i8* %this, i32 8
	%_41 = bitcast i8* %_40 to i32**
	%_42 = load i32*, i32** %_41
	%_43 = load i32, i32* %aux06
	%_44 = getelementptr i8, i8* %this, i32 8
	%_45 = bitcast i8* %_44 to i32**
	%_46 = load i32*, i32** %_45
	%_47 = load i32, i32* %j
	%_48 = add i32 %_47, 1
	%_49 = getelementptr i32, i32* %_46, i32 %_48
	%_50 = load i32, i32* %_49

	%_51 = add i32 %_43, 1
	%_52 = getelementptr i32, i32* %_42, i32 %_51
	store i32 %_50, i32* %_52
	%_54 = getelementptr i8, i8* %this, i32 8
	%_55 = bitcast i8* %_54 to i32**
	%_56 = load i32*, i32** %_55
	%_57 = load i32, i32* %j
	%_58 = load i32, i32* %t
	%_59 = add i32 %_57, 1
	%_60 = getelementptr i32, i32* %_56, i32 %_59
	store i32 %_58, i32* %_60
	br label %if8

if7:
	store i32 0, i32* %nt

	br label %if8

if8:

	%_63 = load i32, i32* %j
	%_62 = add i32 %_63, 1
	store i32 %_62, i32* %j

	br label %if3
if4:

	%_65 = load i32, i32* %i
	%_64 = sub i32 %_65, 1
	store i32 %_64, i32* %i

	br label %if0
if1:

	ret i32 0
}

define i32 @BBS.Print(i8* %this) {
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

define i32 @BBS.Init(i8* %this, i32 %.sz) {
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
