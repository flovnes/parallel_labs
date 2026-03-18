with Ada.Text_IO; use Ada.Text_IO;

procedure main is

   type Stop_Array is array (1 .. 12) of Boolean;
   
   pragma Atomic_Components(Stop_Array);
   
   Stop_Flags : Stop_Array := (others => False);

   task type Worker (ID : Integer; Step_Size : Integer);

   task body Worker is
      Sum          : Long_Long_Integer := 0;
      Count        : Long_Long_Integer := 0;
      Current_Val  : Long_Long_Integer := 0;
   begin
      loop
         Sum := Sum + Current_Val;
         Count := Count + 1;
         Current_Val := Current_Val + Long_Long_Integer(Step_Size);
         delay 0.000001;
         exit when Stop_Flags(ID);
      end loop;

      Put_Line("task " & ID'Img & " stopped. steps =" & Count'Img & ", sum =" & Sum'Img);
   end Worker;

   T1 : Worker(1, 4);
   T2 : Worker(2, 4);
   T3 : Worker(3, 4);
   T4 : Worker(4, 4);
   T5 : Worker(5, 4);
   T6 : Worker(6, 4);
   T7 : Worker(7, 4);
   T8 : Worker(8, 4);
   T9 : Worker(9, 4);
   T10 : Worker(10, 4);
   T11 : Worker(11, 4);
   T12 : Worker(12, 4);

-- manager task
begin
   for I in 1 .. 12 loop
      delay 1.0;
      Stop_Flags(I) := True; -- 'I' == stop
      Put_Line("stopping task " & I'Img);
   end loop;

end main;