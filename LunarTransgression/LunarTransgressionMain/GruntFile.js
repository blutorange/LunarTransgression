module.exports = function(grunt) 
{
    require("load-grunt-tasks")(grunt); 
 
    grunt.initConfig({
        babel: {
            options: {
                sourceMap: false,
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
            options: {
                compress: true,
                mangle: true,
                sourceMap: false,
                preserveComments: false
            },        	
            build: {
                files: {
//                    expand: true,
                    'target/generated-sources/js/resources/translune.min.js': ['target/generated-sources/js/resources/translune/*.js']
                    //src: ['*.js', '**/*.js'],
                    //dest: 'target/generated-sources/js/resources'
                }
            }
        }
    });
      
    grunt.registerTask('default', ['babel', 'uglify']);
}