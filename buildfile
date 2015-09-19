repositories.remote << 'http://repo1.maven.org/maven2'

define 'mapeditor' do
	project.version = '0.0.1'
	package(:jar).with :manifest=>{'Main-Class'=>'isogame.MapEditor'}
end

