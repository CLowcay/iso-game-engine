# © Callum Lowcay 2015, 2016
# 
# This file is part of iso-game-engine.
# 
# iso-game-engine is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# iso-game-engine is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with iso-game-engine.  If not, see <http://www.gnu.org/licenses/>.

repositories.remote << 'http://repo1.maven.org/maven2'

allJars = [
	'lib/json-simple-1.1.1.jar',
	'lib/durian-3.4.0.jar'
]

classpath = allJars.join ' '


define 'mapeditor' do
	compile.using(:lint => 'all').with('lib/json-simple-1.1.1.jar')
	project.version = '0.0.1'
	package(:jar).with(:manifest=>{'Main-Class'=>'isogame.editor.MapEditor', 'Class-Path'=>classpath})
	run.using(:main => 'isogame.editor.MapEditor')
end

