/**
 * 取得した天気データを一時的に保持する変数
 */
let currentWeatherData = null;

/**
 * 地域選択時に呼ばれる関数
 * APIからデータを取得し、プレビューエリアにのみ反映する
 */
async function updateWeatherPreview() {
    const regionSelect = document.getElementById('region-select');
    const previewArea = document.getElementById('weather-preview');
    
    if (!regionSelect || !regionSelect.value) {
        previewArea.textContent = "--";
        currentWeatherData = null;
        return;
    }

    const coords = regionSelect.value.split(',');
    const lat = coords[0];
    const lon = coords[1];
    const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,relative_humidity_2m,weather_code&timezone=Asia/Tokyo`;

    try {
        previewArea.textContent = "取得中...";
        
        const response = await fetch(url);
        if (!response.ok) throw new Error('Network response was not ok');

        const data = await response.json();
        const current = data.current;

		// --- ★追加：気象コードをマークに変換 ---
		let weatherMark = "";
		const code = current.weather_code;

		if (code === 0) {
			weatherMark = "☀️"; // 快晴
		} else if (code >= 1 && code <= 3) {
			weatherMark = "☁️"; // 晴れ〜曇り
		} else if (code >= 45 && code <= 48) {
			weatherMark = "🌫️"; // 霧
		} else if (code >= 51 && code <= 67 || code >= 80 && code <= 82) {
			weatherMark = "☔"; // 雨・しとしと雨
		} else if (code >= 71 && code <= 77 || code >= 85 && code <= 86) {
			weatherMark = "❄️"; // 雪
		} else if (code >= 95 && code <= 99) {
			weatherMark = "⚡"; // 雷雨
		} else {
			weatherMark = "❓";
		}
		
		const temp = current.temperature_2m;
        const humi = current.relative_humidity_2m;
        
        // データを一時保存（コピーボタン用）
        currentWeatherData = { temp, humi };
        
        // プレビュー表示の更新
        previewArea.textContent = `${weatherMark} ${temp}℃ / ${humi}%`;

    } catch (error) {
        console.error('Error fetching weather:', error);
        previewArea.textContent = "取得失敗";
        currentWeatherData = null;
    }
}

/**
 * 「天気を入力欄にコピー」ボタンが押された時に呼ばれる関数
 */
function applyWeatherToFields() {
    const tempInput = document.getElementById('temp-input');
    const humiInput = document.getElementById('humi-input');

    if (currentWeatherData) {
        tempInput.value = currentWeatherData.temp;
        humiInput.value = currentWeatherData.humi;
    } else {
        alert("先に地域を選択して、天気を表示させてください。");
    }
}