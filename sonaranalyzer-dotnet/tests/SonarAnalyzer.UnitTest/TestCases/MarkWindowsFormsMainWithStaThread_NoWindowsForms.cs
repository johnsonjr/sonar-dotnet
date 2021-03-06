﻿using System;

namespace Tests.Diagnostics
{
    class Program_00
    {
        [SomeNonsense] // Error [CS0246,CS0246] - unknown type
        public static void Main()
        {
        }
    }

    class Program_01
    {
        public static void Main()

        {
        }
    }

    class Program_02
    {
        public static int Main(string[] args)
        {
            return 1;
        }
    }

    class Program_03
    {
        [MTAThread]
        public static void Main()
        {
        }
    }

    class Program_04
    {
        [MTAThread]
        public static int Main(string[] args)
        {
            return 1;
        }
    }

    class Program_05
    {
        [System.MTAThreadAttribute]
        public static int Main(string[] args)
        {
            return 1;
        }
    }

    class Program_06
    {
        [STAThread]
        public static void Main()
        {
        }
    }

    class Program_07
    {
        [STAThread]
        public static int Main(string[] args)
        {
            return 1;
        }
    }

    class Program_08
    {
        [System.STAThread]
        public static int Main(string[] args)
        {
            return 1;
        }
    }

    class Program_09
    {
        [STAThread("this is wrong", 1)] // Error [CS1729] - ctor doesn't exist
        public static int Main(string[] args)
        {
            return 1;
        }
    }
}
