with Ada.Text_IO; use Ada.Text_IO;

procedure main is
   Storage_Size : constant Integer := 5;
   Total_Items  : constant Integer := 20;
   Thread_Count : constant Integer := 4;
   Items_Per_Th : constant Integer := Total_Items / Thread_Count;

   protected Storage is
      entry Put(Item : in Integer; ID : in Integer);
      entry Get(ID : in Integer);
   private
      Count : Integer := 0;
   end Storage;

   protected body Storage is
      entry Put(Item : in Integer; ID : in Integer) when Count < Storage_Size is
      begin
         Count := Count + 1;
         Put_Line("Producer" & ID'Img & " added item" & Item'Img);
      end Put;

      entry Get(ID : in Integer) when Count > 0 is
      begin
         Count := Count - 1;
         Put_Line("Consumer" & ID'Img & " took item");
      end Get;
   end Storage;

   task type Producer(ID : Integer);
   task body Producer is
   begin
      for I in 1 .. Items_Per_Th loop
         Storage.Put(I, ID);
      end loop;
   end Producer;

   task type Consumer(ID : Integer);
   task body Consumer is
   begin
      for I in 1 .. Items_Per_Th loop
         Storage.Get(ID);
      end loop;
   end Consumer;

   P1 : Producer(1); P2 : Producer(2);
   C1 : Consumer(1); C2 : Consumer(2);

begin
   null;
end main;