module.exports = function(grunt) 
{
    require("load-grunt-tasks")(grunt); 
 
    grunt.initConfig({
        babel: {
            options: {
                sourceMap: true,
                presets: ['babel-preset-es2015']
            },
            dist: {
            files: [
                {
                    expand: true,
                    cwd: 'WebContent/resources/translune',
                    src: ['*.js', '**/*.js'],
                    dest: 'target/generated-sources/js/resources/translune'
                }
            ]
            }
        },
 
        uglify: {
            build: {
                files: [{
                    expand: true,
                    cwd: 'target/dist/resources/js',
                    src: ['*.js', '**/*.js'],
                    dest: 'target/generated-sources/js/resources/translune'
                }]
            }
        }
    });
      
    grunt.registerTask('default', ['babel', 'uglify']);
}