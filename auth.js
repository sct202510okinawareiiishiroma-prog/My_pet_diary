/*
*ログインと登録にある共通部分の**auth**としてまとめ
*/
document.addEventListener('DOMContentLoaded', () => {
    const togglePassword = document.querySelector('#togglePassword');
    const passwordInput = document.querySelector('#password');

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', function () {
            const isTypePassword = passwordInput.getAttribute('type') === 'password';
            passwordInput.setAttribute('type', isTypePassword ? 'text' : 'password');
            this.textContent = isTypePassword ? '🐵' : '🙈';
        });
    }
});