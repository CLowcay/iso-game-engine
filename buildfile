repositories.remote << 'http://repo1.maven.org/maven2'

define 'mapeditor' do
	compile.using(:lint => 'all').with('lib/json-simple-1.1.1.jar')
	project.version = '0.0.1'
	package(:jar).with(:manifest=>{'Main-Class'=>'isogame.editor.MapEditor'})
	run.using(:main => 'isogame.editor.MapEditor')
end

define 'dataeditor' do
	compile.using(:lint => 'all').with('lib/json-simple-1.1.1.jar')
	project.version = '0.0.1'
	package(:jar).with(:manifest=>{'Main-Class'=>'isogame.dataEditor.DataEditor'})
	run.using(:main => 'isogame.editor.DataEditor')
end

define 'game' do
	compile.using(:lint => 'all').with('lib/json-simple-1.1.1.jar')
	project.version = '0.0.1'
	package(:jar).with(:manifest=>{'Main-Class'=>'isogame.game.Game'})
	run.using(:main => 'isogame.game.Game')
end

