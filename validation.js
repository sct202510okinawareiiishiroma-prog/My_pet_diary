/**
 * MyPetDiary 入力バリデーション
 */
document.addEventListener('DOMContentLoaded', () => {
    const recordForm = document.querySelector('form[action$="/add"]'); // 記録追加フォーム
    
    if (!recordForm) return;

    // 1. 未来の日付を選択不可にする（動的にmax値を更新）
    const dateInput = recordForm.querySelector('input[type="datetime-local"]');
    if (dateInput) {
        const updateMaxDate = () => {
            const now = new Date();
            // YYYY-MM-DDTHH:mm 形式に変換
            const localIsoString = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
                .toISOString().slice(0, 16);
            dateInput.max = localIsoString;
        };
        updateMaxDate();
        // フォームを開くたびに最新にするため、フォーカス時にも更新
        dateInput.addEventListener('focus', updateMaxDate);
    }

    // 2. 数値上限のチェック処理
    const validateNumericInput = (input, max, label) => {
        const value = parseFloat(input.value);
        if (value >= max) {
            alert(`${label}は ${max} 未満（最大 ${max - 0.1}）で入力してください。`);
            input.value = max - 0.1; // 上限値に自動補正
            return false;
        }
        return true;
    };

    // 3. 送信時の最終チェック
    recordForm.addEventListener('submit', (event) => {
        const weightInput = recordForm.querySelector('input[name="weight"]');
        const foodInput = recordForm.querySelector('input[name="food"]');
        const customInputs = recordForm.querySelectorAll('input[name^="customValues"]');

        // 体重チェック (999.9まで)
        if (weightInput && weightInput.value) {
            if (!validateNumericInput(weightInput, 1000, "体重")) {
                event.preventDefault();
                return;
            }
        }

        // ごはんチェック (9999.9まで)
        if (foodInput && foodInput.value) {
            if (!validateNumericInput(foodInput, 10000, "ごはんの量")) {
                event.preventDefault();
                return;
            }
        }

        // カスタム項目チェック (9999.9まで)
        for (let input of customInputs) {
            if (input.value) {
                if (!validateNumericInput(input, 10000, "カスタム項目の値")) {
                    event.preventDefault();
                    return;
                }
            }
        }
    });
});