with Ada.Text_IO; use Ada.Text_IO;
with Ada.Numerics.Discrete_Random;

procedure main is
   Dim : constant Integer := 100000;
   Thread_Count : constant Integer := 4;
   Arr : array(0 .. Dim - 1) of Integer;

   protected Result_Manager is
      procedure Update_Min(Val : in Integer; Idx : in Integer);
      entry Get_Final(Val : out Integer; Idx : out Integer);
   private
      Current_Min : Integer := Integer'Last;
      Min_Idx     : Integer := -1;
      Finished    : Integer := 0;
   end Result_Manager;

   protected body Result_Manager is
      procedure Update_Min(Val : in Integer; Idx : in Integer) is
      begin
         if Val < Current_Min then
            Current_Min := Val;
            Min_Idx := Idx;
         end if;
         Finished := Finished + 1;
      end Update_Min;

      entry Get_Final(Val : out Integer; Idx : out Integer) when Finished = Thread_Count is
      begin
         Val := Current_Min;
         Idx := Min_Idx;
      end Get_Final;
   end Result_Manager;

   task type Finder is
      entry Start(Low, High : Integer);
   end Finder;

   task body Finder is
      L_Low, L_High : Integer;
      L_Min : Integer := Integer'Last;
      L_Idx : Integer := -1;
   begin
      accept Start(Low, High : Integer) do
         L_Low := Low;
         L_High := High;
      end Start;

      for I in L_Low .. L_High loop
         if Arr(I) < L_Min then
            L_Min := Arr(I);
            L_Idx := I;
         end if;
      end loop;
      Result_Manager.Update_Min(L_Min, L_Idx);
   end Finder;

   Workers : array(1 .. Thread_Count) of Finder;
   Final_Min, Final_Idx : Integer;
   Chunk : Integer := Dim / Thread_Count;

begin
   for I in Arr'Range loop
      Arr(I) := I + 10;
   end loop;
   Arr(5432) := -123;

   for I in 1 .. Thread_Count loop
      Workers(I).Start((I-1)*Chunk, (if I = Thread_Count then Dim-1 else I*Chunk - 1));
   end loop;

   Result_Manager.Get_Final(Final_Min, Final_Idx);
   Put_Line("Min: " & Final_Min'Img & " at Index: " & Final_Idx'Img);
end main;