require "json"
require 'tmpdir'

do_json = !ARGV.index("--no-json")
do_img = !ARGV.index("--no-image")

def create_atlas(x,y,tiles,count,basename)
    tiles_y = (count.to_f / tiles.to_f).ceil;
    frames = {}
    count.times.each do |frame_index|
        row = frame_index / tiles
        column = frame_index % tiles
        frame = {
            "frame" => {
                "x" => column*x,
                "y" => row*y,
                "w" => x,
                "h" => y
            },
            "rotated" => false,
            "trimmed" => false,
            "spriteSourceSize" => {
                "x" => 0,
                "y" => 0,
                "w" => x,
                "h" => y
            },
            "sourceSize" => {
                "w" => x,
                "h" => y
            }
        }
        frames["img/#{basename}_%03d.png" % frame_index] = frame;
    end
    atlas = {
        "frames" => frames,
        "meta" => {
            "app" => "ruby",
            "version" => "1.0",
            "image" => "#{basename}.png",
            "format" => "RGBA8888",
            "size" => {
                "w" => x*tiles,
                "h" => y*tiles_y,
            },
            "scale" => "1"
        }
    }
    JSON.generate(atlas);
end

Dir.mktmpdir("sprites") do |tmpdir|
    puts "using temporary directory #{tmpdir}"
    Dir.foreach('character-gif').sort.each do |file|
        if file =~ /\.gif$/
            puts "processing #{file}..."
            FileUtils.rm_rf(Dir.glob("#{tmpdir}/*"))
            basename = File.basename(file, ".gif")
            info = `identify -format '%[fx:w]x%[fx:h]\n' "character-gif/#{file}"`.split(?\n)
            x,y = *info[0].split('x').map(&:to_i)
            count = info.size
            puts "warn: found less than 5 frames in animation" if count < 5
            tiles = Math.sqrt(count.to_f*y.to_f/x.to_f).ceil
            success = true
            if do_img
                success &&= `convert -coalesce \"character-gif/#{file}\" \"#{tmpdir}/%03d.png\"`
                success &&= Kernel.system("montage -background transparent -tile #{tiles}x -geometry +0+0 \"#{tmpdir}/*.png\" output/character-img/#{basename}.png")
                puts "error while processing" unless success
            end
            if do_json
                File.write("./output/character-img/#{basename}.json", create_atlas(x,y,tiles,count,basename))
            end
        end
    end
end
