with Ada.Text_IO; use Ada.Text_IO;
with Ada.Numerics.Discrete_Random;

procedure main is
    package Random_Int is new Ada.Numerics.Discrete_Random (Integer);
    use Random_Int;
    RandGen      : Generator;
    arr_size     : constant Integer := 10000000;
    thread_count : constant Integer := 12;

    type Int_Array is array (0 .. arr_size - 1) of Integer;
    type Int_Ptr is access Int_Array;

    protected res is
        procedure update (val : in Integer; idx : in Integer);
        entry get_result (val : out Integer; idx : out Integer);
    private
        min_val  : Integer := Integer'Last;
        min_idx  : Integer := -1;
        finished : Integer := 0;
    end res;

    protected body res is
        procedure update (val : in Integer; idx : in Integer) is
        begin
            if val < min_val then
                min_val := val;
                min_idx := idx;
            end if;
            finished := finished + 1;
        end update;

        entry get_result (val : out Integer; idx : out Integer)
           when finished = thread_count
        is
        begin
            val := min_val;
            idx := min_idx;
        end get_result;
    end res;

    task type worker is
        entry start (s, e : Integer; arr : Int_Ptr);
    end worker;

    task body worker is
        low, high : Integer;
        m         : Integer := Integer'Last;
        idx       : Integer := -1;
        local_arr : Int_Ptr;
    begin
        accept start (s, e : Integer; arr : Int_Ptr) do
            low := s;
            high := e;
            local_arr := arr;
        end start;

        for i in low .. high loop
            if local_arr (i) < m then
                m := local_arr (i);
                idx := i;
            end if;
        end loop;

        res.update (m, idx);
    end worker;

    arr                  : Int_Ptr;
    workers              : array (1 .. thread_count) of worker;
    chunk                : Integer := arr_size / thread_count;
    final_min, final_idx : Integer;

begin
    Reset (RandGen);
    arr := new Int_Array;

    for i in 0 .. arr_size - 1 loop
        arr (i) := abs (Random (RandGen)) mod 47;
    end loop;
    arr (4747474) := -7;

    for i in 1 .. thread_count loop
        workers (i).start
           ((i - 1) * chunk,
            (if i = thread_count then arr_size - 1 else i * chunk - 1),
            arr);
    end loop;

    res.get_result (final_min, final_idx);
    Put_Line ("min:" & final_min'Img & ", index:" & final_idx'Img);
end main;
