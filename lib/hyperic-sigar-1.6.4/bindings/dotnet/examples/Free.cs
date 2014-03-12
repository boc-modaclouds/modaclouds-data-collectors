/**
 * Copyright ${year} deib-polimi
 * Contact: imperial <weikun.wang11@imperial.ac.uk>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
using System;
using Hyperic.Sigar;

public class Free {

    public static void Main() {
        Sigar sigar = new Sigar();

	Mem mem = sigar.Mem();
	Swap swap = sigar.Swap();

        System.Console.WriteLine("\tTotal\tUsed\tFree");

        System.Console.WriteLine("Mem:\t" +
                                 mem.Total / 1024 + "\t" +
                                 mem.Used / 1024 + "\t" +
                                 mem.Free / 1024);

        System.Console.WriteLine("Swap:\t" +
                                 swap.Total / 1024 + "\t" +
                                 swap.Used / 1024 + "\t" +
                                 swap.Free / 1024);

        System.Console.WriteLine("RAM:\t" + mem.Ram + "MB");
    }
}
