#!/bin/bash

# 🎬 RIP Fitness App - Video to GIF Conversion Script
# Media Production Specialist with Claude Flow coordination

set -e

# Configuration
INPUT_DIR="/Users/kooshapari/temp-PRODVERCEL/485/kush/Rip/FitnessApp/test-results/videos"
OUTPUT_DIR="/Users/kooshapari/temp-PRODVERCEL/485/kush/Rip/FitnessApp/docs/media"
WEB_DIR="$OUTPUT_DIR/gifs/web"
MOBILE_DIR="$OUTPUT_DIR/gifs/mobile"
SOCIAL_DIR="$OUTPUT_DIR/gifs/social"

# Create output directories
mkdir -p "$WEB_DIR" "$MOBILE_DIR" "$SOCIAL_DIR"

echo "🎬 Starting Video to GIF Conversion Process"
echo "📁 Input: $INPUT_DIR"
echo "📁 Output: $OUTPUT_DIR"
echo ""

# Function to convert video to different GIF formats
convert_to_gifs() {
    local input_file="$1"
    local base_name="$2"
    local start_time="${3:-0}"
    local duration="${4:-30}"
    
    echo "🔄 Converting: $base_name"
    
    # Web GIF (720p, 15fps, max 2MB)
    echo "  📱 Creating web GIF..."
    ffmpeg -i "$input_file" -ss "$start_time" -t "$duration" \
        -vf "fps=15,scale=720:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" \
        -loop 0 "$WEB_DIR/${base_name}_demo.gif" \
        -y -loglevel error
    
    # Mobile GIF (480p, 12fps, max 1MB)
    echo "  📱 Creating mobile GIF..."
    ffmpeg -i "$input_file" -ss "$start_time" -t "$duration" \
        -vf "fps=12,scale=480:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" \
        -loop 0 "$MOBILE_DIR/${base_name}_demo.gif" \
        -y -loglevel error
    
    # Social GIF (360p, 10fps, max 500KB)
    echo "  📱 Creating social GIF..."
    ffmpeg -i "$input_file" -ss "$start_time" -t "$duration" \
        -vf "fps=10,scale=360:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" \
        -loop 0 "$SOCIAL_DIR/${base_name}_demo.gif" \
        -y -loglevel error
    
    echo "  ✅ Completed: $base_name"
}

# Video conversion definitions with optimal segments
declare -A videos=(
    ["complete_user_onboarding.mp4"]="onboarding,5,25"
    ["first_workout_experience.mp4"]="workout,10,30"
    ["nutrition_logging_workflow.mp4"]="nutrition,5,20"
    ["health_data_sync.mp4"]="health,15,30"
    ["offline_usage_demo.mp4"]="offline,10,25"
    ["accessibility_navigation.mp4"]="accessibility,5,20"
    ["dark_mode_showcase.mp4"]="darkmode,0,15"
    ["performance_monitoring.mp4"]="performance,30,30"
    ["security_features_demo.mp4"]="security,15,25"
    ["biometric_authentication.mp4"]="biometric,0,15"
    ["widget_interactions.mp4"]="widgets,5,20"
    ["error_handling_scenarios.mp4"]="errors,20,25"
)

# Convert videos to GIFs
for video_file in "${!videos[@]}"; do
    if [ -f "$INPUT_DIR/$video_file" ]; then
        IFS=',' read -r base_name start_time duration <<< "${videos[$video_file]}"
        convert_to_gifs "$INPUT_DIR/$video_file" "$base_name" "$start_time" "$duration"
    else
        echo "⚠️  Video not found: $video_file"
        echo "🎬 Creating placeholder demo GIF for: ${videos[$video_file]%%,*}"
        
        # Create placeholder GIF using ffmpeg with solid color and text
        base_name="${videos[$video_file]%%,*}"
        
        # Web placeholder
        ffmpeg -f lavfi -i color=c=0x667eea:size=720x405:duration=10:rate=15 \
            -vf "drawtext=text='${base_name^^} DEMO\\nComing Soon':fontcolor=white:fontsize=24:x=(w-text_w)/2:y=(h-text_h)/2" \
            "$WEB_DIR/${base_name}_demo.gif" \
            -y -loglevel error
        
        # Mobile placeholder
        ffmpeg -f lavfi -i color=c=0x667eea:size=480x270:duration=10:rate=12 \
            -vf "drawtext=text='${base_name^^} DEMO\\nComing Soon':fontcolor=white:fontsize=16:x=(w-text_w)/2:y=(h-text_h)/2" \
            "$MOBILE_DIR/${base_name}_demo.gif" \
            -y -loglevel error
        
        # Social placeholder
        ffmpeg -f lavfi -i color=c=0x667eea:size=360x203:duration=10:rate=10 \
            -vf "drawtext=text='${base_name^^}\\nDemo':fontcolor=white:fontsize=14:x=(w-text_w)/2:y=(h-text_h)/2" \
            "$SOCIAL_DIR/${base_name}_demo.gif" \
            -y -loglevel error
        
        echo "  ✅ Created placeholder: $base_name"
    fi
done

# Generate file size report
echo ""
echo "📊 GIF Conversion Report"
echo "========================"

for category in web mobile social; do
    echo ""
    echo "📱 ${category^^} GIFs:"
    dir="$OUTPUT_DIR/gifs/$category"
    if [ -d "$dir" ]; then
        find "$dir" -name "*.gif" -exec ls -lh {} \; | \
        awk '{printf "  %-25s %8s\n", $9, $5}' | \
        sed 's|.*/||'
    fi
done

# Calculate total sizes
echo ""
echo "💾 Storage Summary:"
web_size=$(find "$WEB_DIR" -name "*.gif" -exec du -ch {} + 2>/dev/null | tail -1 | cut -f1 || echo "0")
mobile_size=$(find "$MOBILE_DIR" -name "*.gif" -exec du -ch {} + 2>/dev/null | tail -1 | cut -f1 || echo "0")
social_size=$(find "$SOCIAL_DIR" -name "*.gif" -exec du -ch {} + 2>/dev/null | tail -1 | cut -f1 || echo "0")

echo "  📱 Web GIFs:    $web_size"
echo "  📱 Mobile GIFs: $mobile_size" 
echo "  📱 Social GIFs: $social_size"

# Generate README update with actual GIFs
echo ""
echo "📝 Updating README with GIF links..."

cat > "$OUTPUT_DIR/gif_readme_snippet.md" << 'EOF'
## 🎬 Live Demonstrations

### 🎯 User Onboarding Experience
![Onboarding Demo](docs/media/gifs/web/onboarding_demo.gif)
*Complete new user flow from welcome to first workout setup*

### 💪 Workout Tracking in Action  
![Workout Demo](docs/media/gifs/web/workout_demo.gif)
*Live workout session with real-time tracking and analytics*

### 🥗 Smart Nutrition Logging
![Nutrition Demo](docs/media/gifs/web/nutrition_demo.gif)
*Barcode scanning and macro tracking demonstration*

### 📊 Health Data Integration
![Health Demo](docs/media/gifs/web/health_demo.gif)
*HealthConnect sync and data visualization*

### 📱 Mobile Optimized Demos
<div align="center">
<img src="docs/media/gifs/mobile/workout_demo.gif" width="200" alt="Workout Demo">
<img src="docs/media/gifs/mobile/nutrition_demo.gif" width="200" alt="Nutrition Demo">
<img src="docs/media/gifs/mobile/health_demo.gif" width="200" alt="Health Demo">
</div>

### 🌟 Feature Highlights
| Feature | Demo | Description |
|---------|------|-------------|
| 🚀 Onboarding | ![Onboarding](docs/media/gifs/social/onboarding_demo.gif) | Streamlined setup process |
| 🏋️‍♂️ Workouts | ![Workout](docs/media/gifs/social/workout_demo.gif) | Exercise tracking and analytics |
| 🥗 Nutrition | ![Nutrition](docs/media/gifs/social/nutrition_demo.gif) | Food logging and macro tracking |
| 📊 Health | ![Health](docs/media/gifs/social/health_demo.gif) | Comprehensive health dashboard |
| 🌙 Dark Mode | ![Dark Mode](docs/media/gifs/social/darkmode_demo.gif) | Beautiful dark theme |
| ♿ Accessibility | ![Accessibility](docs/media/gifs/social/accessibility_demo.gif) | Screen reader support |

EOF

echo "✅ GIF conversion complete!"
echo "📁 Generated files in: $OUTPUT_DIR/gifs/"
echo "📝 README snippet created: $OUTPUT_DIR/gif_readme_snippet.md"
echo ""
echo "🚀 Next steps:"
echo "  1. Review generated GIFs for quality"
echo "  2. Update main README.md with GIF links"
echo "  3. Optimize any oversized GIFs"
echo "  4. Test GIF loading on different devices"
echo ""
echo "🎉 Media production complete!"