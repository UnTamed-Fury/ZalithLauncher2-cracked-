const container = document.getElementById("skin-container");

const getWidth = () => container.clientWidth || window.innerWidth || 300;
const getHeight = () => container.clientHeight || window.innerHeight || 400;

const skinViewer = new skinview3d.SkinViewer({
    canvas: document.createElement("canvas"),
    width: getWidth(),
    height: getHeight(),
    skin: "steve.png"
});

container.appendChild(skinViewer.canvas);

// Default Walking Animation
skinViewer.animation = new skinview3d.WalkingAnimation();
skinViewer.animation.speed = 0.8;

skinViewer.controls.enableRotate = true;
skinViewer.controls.enableZoom = false;
skinViewer.controls.enablePan = false;

// Center the camera
skinViewer.camera.position.set(0, 10, 50);
if (skinViewer.camera.lookAt) {
    skinViewer.camera.lookAt(0, 16, 0);
}

// Double-click / double-tap to reset to front view
function resetCamera() {
    skinViewer.camera.position.set(0, 10, 50);
    skinViewer.controls.target.set(0, 16, 0);
    skinViewer.controls.update();
}

skinViewer.canvas.addEventListener("dblclick", resetCamera);

let lastTap = 0;
skinViewer.canvas.addEventListener("touchend", () => {
    const now = Date.now();
    if (now - lastTap < 300) resetCamera();
    lastTap = now;
});

function resize() {
    const w = getWidth();
    const h = getHeight();
    if (w > 0 && h > 0) {
        skinViewer.width = w;
        skinViewer.height = h;
    }
}

window.addEventListener('resize', resize);
setTimeout(resize, 100);
setTimeout(resize, 500);

function loadSkin(skinUrl, model = "auto-detect") {
    skinViewer.loadSkin(skinUrl, { model: model });
}

function loadSkinFromData(base64Data, model = "auto-detect") {
    skinViewer.loadSkin(base64Data, { model: model });
}

function loadCape(capeUrl) {
    skinViewer.loadCape(capeUrl);
}