with Ada.Text_IO; use Ada.Text_IO;
with Ada.Numerics.Discrete_Random;

procedure main is
    Dim        : constant Integer := 100000;
    Thread_Num : constant Integer := 4;

    type Int_Array is array (1 .. Dim) of Integer;
    Arr : Int_Array;

    procedure Init_Arr is
        package Random_Int is new Ada.Numerics.Discrete_Random (Integer);
        G : Random_Int.Generator;
    begin
        Random_Int.Reset (G);
        for I in 1 .. Dim loop
            Arr (I) := I + 10;
        end loop;

        Arr (Dim / 2 + 7) := -474;
    end Init_Arr;

    protected Min_Manager is
        procedure Set_Part_Min (Val : in Integer; Idx : in Integer);
        entry Get_Result (Min_Val : out Integer; Min_Idx : out Integer);
    private
        Global_Min     : Integer := Integer'Last;
        Global_Min_Idx : Integer := -1;
        Tasks_Count    : Integer := 0;
    end Min_Manager;

    protected body Min_Manager is
        procedure Set_Part_Min (Val : in Integer; Idx : in Integer) is
        begin
            if Val < Global_Min then
                Global_Min := Val;
                Global_Min_Idx := Idx;
            end if;
            Tasks_Count := Tasks_Count + 1;
        end Set_Part_Min;

        entry Get_Result (Min_Val : out Integer; Min_Idx : out Integer)
           when Tasks_Count = Thread_Num
        is
        begin
            Min_Val := Global_Min;
            Min_Idx := Global_Min_Idx;
        end Get_Result;
    end Min_Manager;

    task type Finder_Thread is
        entry Start (Start_Index, Finish_Index : in Integer);
    end Finder_Thread;

    task body Finder_Thread is
        L_Start, L_Finish : Integer;
        Local_Min         : Integer := Integer'Last;
        Local_Idx         : Integer := -1;
    begin
        accept Start (Start_Index, Finish_Index : in Integer) do
            L_Start := Start_Index;
            L_Finish := Finish_Index;
        end Start;

        for I in L_Start .. L_Finish loop
            if Arr (I) < Local_Min then
                Local_Min := Arr (I);
                Local_Idx := I;
            end if;
        end loop;

        Min_Manager.Set_Part_Min (Local_Min, Local_Idx);
    end Finder_Thread;

    procedure Parallel_Min is
        Threads                : array (1 .. Thread_Num) of Finder_Thread;
        Chunk_Size             : Integer := Dim / Thread_Num;
        Result_Min, Result_Idx : Integer;
        Low, High              : Integer;
    begin
        for I in 1 .. Thread_Num loop
            Low := (I - 1) * Chunk_Size + 1;
            if I = Thread_Num then
                High := Dim;
            else
                High := I * Chunk_Size;
            end if;

            Threads (I).Start (Low, High);
        end loop;

        Min_Manager.Get_Result (Result_Min, Result_Idx);

        Put_Line ("value:" & Result_Min'Img & ", index -> " & Result_Idx'Img);
    end Parallel_Min;

begin
    Init_Arr;
    Parallel_Min;
end main;
