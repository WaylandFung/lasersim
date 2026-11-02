from PIL import Image
from pathlib import Path

files = sorted(Path("frames").glob("frame_*.png"))

if not files:
    raise RuntimeError("No frames found in the frames folder.")

images = [Image.open(f).convert("RGB") for f in files]

images[0].save(
    "Ez_animation.gif",
    save_all=True,
    append_images=images[1:],
    duration=80,
    loop=0
)

print(f"Created Ez_animation.gif with {len(images)} frames.")