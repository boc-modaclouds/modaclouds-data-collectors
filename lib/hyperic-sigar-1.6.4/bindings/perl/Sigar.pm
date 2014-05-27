#
# Copyright ${year} imperial
# Contact: imperial <weikun.wang11@imperial.ac.uk>
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

package Sigar;

use 5.003;
use strict;
use vars qw($VERSION @ISA);

$VERSION = '0.01';

eval {
    require XSLoader;
};

if ($@) {
    #$] < 5.6
    require DynaLoader;
    @ISA = qw(DynaLoader);
    __PACKAGE__->bootstrap($VERSION);
}
else {
    XSLoader::load(__PACKAGE__, $VERSION);
}

1;
__END__
