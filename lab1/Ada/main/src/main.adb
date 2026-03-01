with Ada.Text_IO; use Ada.Text_IO;

procedure main is

   type Stop_Array is array (1 .. 3) of Boolean;
   
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
         exit when Stop_Flags(ID);
      end loop;

      Put_Line("task " & ID'Img & " stopped. steps =" & Count'Img & ", sum =" & Sum'Img);
   end Worker;

   T1 : Worker(1, 4);
   T2 : Worker(2, 4);
   T3 : Worker(3, 4);

-- manager task
begin
   for I in 1 .. 3 loop
      delay 3.0;
      
      Stop_Flags(I) := True; -- 'I' == stop
      Put_Line("stopping task " & I'Img);
   end loop;

end main;