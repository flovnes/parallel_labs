with Ada.Text_IO;  use Ada.Text_IO;
with Ada.Calendar; use Ada.Calendar;

procedure lab1 is
    type Time_Array is array (1 .. 8) of Duration;
    durations  : constant Time_Array :=
       (4.0, 4.0, 7.0, 4.0, 7.0, 4.0, 7.0, 4.0);
    type Stop_Array is array (1 .. 8) of Boolean;
    pragma Atomic_Components (Stop_Array);
    stop_flags : Stop_Array := (others => False);

    task type Worker is
        entry Init (id_val : Integer; step_val : Integer);
    end Worker;

    task body Worker is
        id, step      : Integer;
        sum           : Long_Long_Integer := 0;
        count         : Long_Long_Integer := 0;
        current_value : Long_Long_Integer := 0;
    begin
        accept Init (id_val : Integer; step_val : Integer) do
            id := id_val;
            step := step_val;
        end Init;

        loop
            sum := sum + current_value;
            count := count + 1;
            current_value := current_value + Long_Long_Integer (step);

            exit when stop_flags (id);
        end loop;

        Put_Line
           ("thread"
            & id'Img
            & " stopped, elements:"
            & count'Img
            & ", sum:"
            & sum'Img);
    end Worker;

    workers : array (1 .. 8) of Worker;

    start_time    : Time := Clock;
    elapsed       : Duration;
    stopped       : array (1 .. 8) of Boolean := (others => False);
    stopped_count : Integer := 0;

begin
    for i in 1 .. 8 loop
        workers (i).Init (i, 2);
    end loop;

    while stopped_count < 8 loop
        elapsed := Clock - start_time;

        for i in 1 .. 8 loop
            if not stopped (i) and then elapsed >= durations (i) then
                stop_flags (i) := True;
                stopped (i) := True;
                stopped_count := stopped_count + 1;
                Put_Line
                   ("stopped thread" & i'Img & " at" & elapsed'Img & " sec");
            end if;
        end loop;

    end loop;
end lab1;
