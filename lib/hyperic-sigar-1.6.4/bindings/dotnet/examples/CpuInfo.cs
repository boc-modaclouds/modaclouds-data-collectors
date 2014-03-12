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

public class CpuInfo {

    public static void Main() {
        Sigar sigar = new Sigar();

        Hyperic.Sigar.CpuInfo[] infos = 
            sigar.CpuInfoList();

        System.Console.WriteLine(infos.Length + " total CPUs..");

        foreach (Hyperic.Sigar.CpuInfo info in infos) {
            System.Console.WriteLine("Vendor........" + info.Vendor);
            System.Console.WriteLine("Model........." + info.Model);
            System.Console.WriteLine("Mhz..........." + info.Mhz);
            System.Console.WriteLine("");
        }
    }
}
