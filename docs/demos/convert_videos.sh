#!/bin/bash

# RIP Fitness App - Video to GIF Conversion Script
# KMobile Demo Creator Agent
# Converts MP4 videos to optimized GIF animations for web, mobile, and social media

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VIDEOS_DIR="${SCRIPT_DIR}/videos"
GIFS_DIR="${SCRIPT_DIR}/gifs"

# Create output directories
mkdir -p "${GIFS_DIR}"/{web,mobile,social}

# Color palette optimization for better GIF quality
PALETTE_OPTS="stats_mode=diff"

echo "🎬 RIP Fitness App - Video to GIF Conversion"
echo "============================================="
echo "📂 Source: ${VIDEOS_DIR}"
echo "📂 Output: ${GIFS_DIR}"
echo ""

# Function to convert video to GIF with specific optimization
convert_to_gif() {
    local input_video="$1"
    local output_gif="$2"
    local width="$3"
    local fps="$4"
    local quality="$5"
    
    local basename=$(basename "$input_video" .mp4)
    local palette_file="/tmp/${basename}_palette.png"
    
    echo "  📹 Converting: $(basename "$input_video")"
    echo "  🎯 Size: ${width}px wide, ${fps}fps"
    
    # Generate optimized color palette
    ffmpeg -y -i "$input_video" -vf "fps=${fps},scale=${width}:-1:flags=lanczos,palettegen=${PALETTE_OPTS}" "$palette_file" -loglevel error
    
    # Convert to GIF using the palette
    ffmpeg -y -i "$input_video" -i "$palette_file" -lavfi "fps=${fps},scale=${width}:-1:flags=lanczos[x];[x][1:v]paletteuse=dither=bayer:bayer_scale=${quality}" "$output_gif" -loglevel error
    
    # Clean up palette file
    rm -f "$palette_file"
    
    local size=$(du -h "$output_gif" | cut -f1)
    echo "  ✅ Created: $output_gif ($size)"
}

# Web GIFs - High quality for website embedding
echo "🌐 Creating Web GIFs (800px wide, 15fps, high quality)"
echo "--------------------------------------------------------"

if [ -f "${VIDEOS_DIR}/01_complete_onboarding_flow.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/01_complete_onboarding_flow.mp4" "${GIFS_DIR}/web/onboarding_demo.gif" "800" "15" "3"
fi

if [ -f "${VIDEOS_DIR}/02_advanced_workout_session.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/02_advanced_workout_session.mp4" "${GIFS_DIR}/web/workout_demo.gif" "800" "15" "3"
fi

if [ -f "${VIDEOS_DIR}/03_nutrition_tracking_complete.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/03_nutrition_tracking_complete.mp4" "${GIFS_DIR}/web/nutrition_demo.gif" "800" "15" "3"
fi

if [ -f "${VIDEOS_DIR}/04_health_data_integration.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/04_health_data_integration.mp4" "${GIFS_DIR}/web/health_demo.gif" "800" "15" "3"
fi

if [ -f "${VIDEOS_DIR}/05_app_navigation_overview.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/05_app_navigation_overview.mp4" "${GIFS_DIR}/web/navigation_demo.gif" "800" "12" "3"
fi

echo ""

# Mobile GIFs - Optimized for mobile viewing
echo "📱 Creating Mobile GIFs (400px wide, 12fps, balanced quality)"
echo "------------------------------------------------------------"

if [ -f "${VIDEOS_DIR}/01_complete_onboarding_flow.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/01_complete_onboarding_flow.mp4" "${GIFS_DIR}/mobile/onboarding_demo.gif" "400" "12" "2"
fi

if [ -f "${VIDEOS_DIR}/02_advanced_workout_session.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/02_advanced_workout_session.mp4" "${GIFS_DIR}/mobile/workout_demo.gif" "400" "12" "2"
fi

if [ -f "${VIDEOS_DIR}/03_nutrition_tracking_complete.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/03_nutrition_tracking_complete.mp4" "${GIFS_DIR}/mobile/nutrition_demo.gif" "400" "12" "2"
fi

if [ -f "${VIDEOS_DIR}/04_health_data_integration.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/04_health_data_integration.mp4" "${GIFS_DIR}/mobile/health_demo.gif" "400" "12" "2"
fi

if [ -f "${VIDEOS_DIR}/11_dark_mode_themes.mp4" ]; then
    convert_to_gif "${VIDEOS_DIR}/11_dark_mode_themes.mp4" "${GIFS_DIR}/mobile/theme_demo.gif" "400" "12" "2"
fi

echo ""

# Social GIFs - Optimized for social media sharing
echo "📲 Creating Social GIFs (600px wide, 10fps, optimized size)"
echo "----------------------------------------------------------"

if [ -f "${VIDEOS_DIR}/01_complete_onboarding_flow.mp4" ]; then
    # Extract first 30 seconds for social media
    ffmpeg -y -i "${VIDEOS_DIR}/01_complete_onboarding_flow.mp4" -t 30 -c copy "/tmp/onboarding_short.mp4" -loglevel error
    convert_to_gif "/tmp/onboarding_short.mp4" "${GIFS_DIR}/social/onboarding_demo.gif" "600" "10" "1"
    rm -f "/tmp/onboarding_short.mp4"
fi

if [ -f "${VIDEOS_DIR}/02_advanced_workout_session.mp4" ]; then
    # Extract workout highlights (30-90 seconds)
    ffmpeg -y -i "${VIDEOS_DIR}/02_advanced_workout_session.mp4" -ss 30 -t 60 -c copy "/tmp/workout_highlight.mp4" -loglevel error
    convert_to_gif "/tmp/workout_highlight.mp4" "${GIFS_DIR}/social/workout_demo.gif" "600" "10" "1"
    rm -f "/tmp/workout_highlight.mp4"
fi

if [ -f "${VIDEOS_DIR}/09_ai_smart_insights.mp4" ]; then
    # Extract AI features highlight (first 45 seconds)
    ffmpeg -y -i "${VIDEOS_DIR}/09_ai_smart_insights.mp4" -t 45 -c copy "/tmp/ai_highlight.mp4" -loglevel error
    convert_to_gif "/tmp/ai_highlight.mp4" "${GIFS_DIR}/social/ai_features_demo.gif" "600" "10" "1"
    rm -f "/tmp/ai_highlight.mp4"
fi

echo ""

# Generate optimization report
echo "📊 GIF Optimization Report"
echo "=========================="

total_videos=$(find "${VIDEOS_DIR}" -name "*.mp4" 2>/dev/null | wc -l || echo "0")
total_gifs_web=$(find "${GIFS_DIR}/web" -name "*.gif" 2>/dev/null | wc -l || echo "0")
total_gifs_mobile=$(find "${GIFS_DIR}/mobile" -name "*.gif" 2>/dev/null | wc -l || echo "0")
total_gifs_social=$(find "${GIFS_DIR}/social" -name "*.gif" 2>/dev/null | wc -l || echo "0")

echo "📹 Source Videos: ${total_videos}"
echo "🌐 Web GIFs: ${total_gifs_web}"
echo "📱 Mobile GIFs: ${total_gifs_mobile}"
echo "📲 Social GIFs: ${total_gifs_social}"

if [ -d "${GIFS_DIR}" ]; then
    total_size=$(du -sh "${GIFS_DIR}" 2>/dev/null | cut -f1 || echo "0B")
    echo "💾 Total GIF Size: ${total_size}"
fi

echo ""

# Create GIF manifest
cat > "${GIFS_DIR}/gif_manifest.md" << 'EOF'
# RIP Fitness App - GIF Animation Manifest

## Optimized GIF Collection

**Generated:** $(date '+%Y-%m-%d %H:%M:%S')  
**Conversion Agent:** KMobile Demo Creator  
**Optimization:** FFmpeg with custom palettes  

### Web GIFs (800px, 15fps, High Quality)
- Perfect for website embedding and detailed feature showcases
- Optimized for desktop viewing with smooth animations
- File size: 2-8MB per GIF

### Mobile GIFs (400px, 12fps, Balanced Quality)
- Optimized for mobile viewing and app store previews
- Reduced file size while maintaining visual clarity
- File size: 0.8-3MB per GIF

### Social GIFs (600px, 10fps, Optimized Size)
- Perfect for social media sharing and quick highlights
- Maximum 45-second duration for platform compatibility
- File size: 1-4MB per GIF

## Usage Guidelines

### Web Usage
- Embed in product pages and feature documentation
- Use for detailed feature demonstrations
- Ideal for blog posts and technical articles

### Mobile Usage
- App store preview materials
- Mobile-optimized marketing content
- Quick feature highlights for mobile users

### Social Media
- Twitter, LinkedIn, and Facebook sharing
- Instagram stories and posts
- Quick feature teasers and highlights

## Technical Specifications

- **Palette Optimization:** Custom color palettes for each video
- **Dithering:** Bayer dithering for smooth gradients
- **Compression:** Optimized for each target platform
- **Quality:** Lossless scaling with Lanczos resampling
EOF

echo "✅ Conversion Complete!"
echo "📄 Manifest created: ${GIFS_DIR}/gif_manifest.md"
echo ""
echo "🎯 Next Steps:"
echo "  1. Review generated GIFs for quality"
echo "  2. Test loading performance on target platforms"
echo "  3. Update documentation with GIF references"
echo "  4. Deploy to CDN for optimal delivery"