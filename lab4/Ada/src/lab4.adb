with Ada.Text_IO; use Ada.Text_IO;
with Ada.Numerics.Float_Random;

procedure lab4 is
   use Ada.Numerics.Float_Random;

   protected type Fork is
      entry Pick;
      procedure Put;
   private
      Available : Boolean := True;
   end Fork;

   protected body Fork is
      entry Pick when Available is
      begin
         Available := False;
      end Pick;
      procedure Put is
      begin
         Available := True;
      end Put;
   end Fork;

   protected Waiter is
      entry Enter;
      procedure Leave;
   private
      Count : Integer := 0;
   end Waiter;

   protected body Waiter is
      entry Enter when Count < 4 is
      begin
         Count := Count + 1;
      end Enter;
      procedure Leave is
      begin
         Count := Count - 1;
      end Leave;
   end Waiter;

   Forks : array (0 .. 4) of Fork;

   task type Philosopher(ID : Integer);
   task body Philosopher is
      Left  : Integer := (ID + 1) mod 5;
      Right : Integer := ID;
      Seed  : Generator;
   begin
      Reset(Seed);
      for I in 1 .. 5 loop
         Put_Line("filosof" & ID'Img & " dymae");
         delay Duration(Random(Seed) * 0.2);

         Waiter.Enter;
         Forks(Right).Pick;
         Forks(Left).Pick;

         Put_Line("filosof " & ID'Img & " zhue");
         delay Duration(Random(Seed) * 0.2);

         Forks(Left).Put;
         Forks(Right).Put;
         Waiter.Leave;
      end loop;
   end Philosopher;

   P0 : Philosopher(0); P1 : Philosopher(1); P2 : Philosopher(2);
   P3 : Philosopher(3); P4 : Philosopher(4);

begin
   null;
end lab4;
