// Список доходів або дитрат
const countTransactions = (walletName, sign) => {
    $.getJSON('/transactions_by_purpose', {walletName: walletName, sign: sign}, function (hashMap) {
        $('.sort-transactions-of-purposes_add-purposes').text('');
        $.each(hashMap, function (key, value) {
            let listPurposes
            if (sign === '+') {
                listPurposes = $('<p class="forEach-purpose">').html(key + ': ' + '<span class="forEach-purpose_add-sum">' + value + '</span>');
            } else {
                listPurposes = $('<p class="forEach-purpose">').html(key + ': ' + '<span class="forEach-purpose_remove-sum">' + value + '</span>');
            }

            $('.sort-transactions-of-purposes_add-purposes').append(listPurposes);

        });
    });
}


// Вибір гаманця
let currentWallet;
const clickOnWallet = (event) => {
    let walletName = event.target.value || event.target.textContent;
    $('.wallet_with_current_working').text(walletName);
    $('.currentWallet').val(walletName);
    countTransactions(walletName, $('.addOrRemove').val());
    console.log(walletName);
    $.getJSON('/get_wallet', {walletName: walletName}, function (wallet) {
        currentWallet = wallet;
        $('.chose_wallet_name').text(wallet.name);
        $('.sum_of_wallet').text(wallet.sum.toFixed(2) + wallet.currency);
        $('.report_list').text('');
    });

    $.getJSON('/get_all_transaction_by_wallet', {
        walletName: walletName,
        page: 0,
        howMuch: 10
    }, function (walletTransactions) {
        let result = walletTransactions.map((element) => {
            let li = $('<li>').addClass('li_of_transactions');
            let spanDate = $('<span>').addClass('date').text(element.date + ' ');
            let spanPurpose = $('<span>').text(element.purpose + ' ');
            let spanVar;
            if (element.sign === '+') {
                spanVar = $('<span>').addClass('arrow_left').html(' &lArr; ');
            } else {
                spanVar = $('<span>').addClass('arrow_right').html(' &rArr; ');
            }
            let spanSum = $('<span>').addClass('sum_that_was_add').text(element.suma.toFixed(2) + currentWallet.currency);
            li.append(spanDate);
            li.append(spanPurpose);
            li.append(spanVar);
            li.append(spanSum);
            return li;
        });

        $('.report_list').prepend(result);
    });

}

// Вибір призначення
const clickOnPurpose = (event) => {
    $('.using_purpose').text(event.target.textContent);
    $('.user_purpose').val(event.target.textContent);

}
$('.wallet_name').on('click', clickOnWallet);
$('.purpose').on('click', clickOnPurpose);


// Додавання нового гаманця
const newWallet = {
    name: 'Name',
    currency: '1'
}
let nameOfNewWallet = 'грн';
const addNewWallet = async (event) => {
    event.preventDefault();
    const inputNewWalletName = $('.new_wallet_name').val().trim();
    if (inputNewWalletName === '') {
        await Swal.fire({
            text: 'Введіть назву гаманця.',
            icon: 'error',
            confirmButtonText: 'Зрозуміло'
        })

        return;
    }
    newWallet.name = inputNewWalletName;
    newWallet.currency = $('input[name="currency"]:checked').val();

    $.post({
        url: '/add_new_wallet',
        contentType: 'application/json',
        data: JSON.stringify(newWallet),
        success: function (response) {
            if (response === 'false') {
                Swal.fire({
                   text: 'Гаманець з назвою з такою назвою вже існує.',
                    icon: 'error',
                    confirmButtonText: 'Зрозуміло'
                });
                $('.new_wallet_name').val("");
                return;
            }
            let innerDiv = $('<div>').addClass('wallet_name').text(response);
            let newDiv = $('<div>').addClass('view_wallets');
            newDiv.on('click', clickOnWallet);
            newDiv.append(innerDiv);
            $('.list_of_wallets').append(newDiv);
            nameOfNewWallet = inputNewWalletName;
            $('.new_wallet_name').val("");

        },
        error: function (xhr, status, error) {
            console.log(error);
        }

    });

}

// Транзакция коштів
const walletTransaction = {
    walletName: 'Гаманець',
    sum: 0,
    purpose: '',
    sign: '+'

}

const addWalletTransaction = async (event) => {
    event.preventDefault();
    const inputSum = $('.sum_for_transaction').val().trim();
    if (inputSum === '') {
        const checkSum = await Swal.fire({
            text: 'Заповніть поле сумма.',
            icon: 'error',
            confirmButtonText: 'Зрозуміло'
        })
        if (checkSum.isConfirmed) {
            return;
        }
    }

    walletTransaction.walletName = $('.currentWallet').val();
    walletTransaction.sum = inputSum;
    walletTransaction.purpose = $('.user_purpose').val();
    walletTransaction.sign = $('.addOrRemove').val();

    $.post({
        url: '/money_transaction',
        contentType: 'application/json',
        data: JSON.stringify(walletTransaction),
        success: function (response) {
            countTransactions($('.currentWallet').val(), $('.addOrRemove').val());
            $('.sum_for_transaction').val('');
            $('.sum_of_wallet').text(response.sum.toFixed(2) + response.currency);
            let list = $('<li>').addClass('li_of_transactions');
            let spanDate = $('<span>').addClass('date').text(response.strDate + ' ');
            let spanPurpose = $('<span>').text(walletTransaction.purpose + ' ');
            let spanVar;
            if (walletTransaction.sign === '+') {
                spanVar = $('<span>').addClass('arrow_left').html(' &lArr; ');
            } else {
                spanVar = $('<span>').addClass('arrow_right').html(' &rArr; ');
            }
            let parseSum = parseFloat(walletTransaction.sum);
            let spanSum = $('<span>').addClass('sum_that_was_add').text(parseSum.toFixed(2) + response.currency);
            list.append(spanDate);
            list.append(spanPurpose);
            list.append(spanVar);
            list.append(spanSum);
            $('.report_list').prepend(list);

        },
        error: function (xhr, status, error) {
            console.log(error);
        }
    });

}

// Додавання нового призначення
const addNewPurpose = () => {
    $('.list-of-purposes_purpose-name').toggleClass('list-of-purposes__disable').toggleClass('list-of-purposes__active');
    $('.list-of-purposes_btn-add-purpose').toggleClass('list-of-purposes__disable').toggleClass('list-of-purposes__active');
    let userPurpose = $('.list-of-purposes_purpose-name').val().trim();
    if (userPurpose.trim() === '') {
        return;
    }
    fetch('/add_new_purpose?purpose=' + userPurpose, {method: 'POST'}).then((response) => {
        if (response.ok) {
            $('.list-of-purposes_purpose-name').val('');
            let viewPurpose = $('<div>').addClass('view-purpose');
            let purpose = $('<div>').addClass('purpose').text(userPurpose);
            viewPurpose.on('click', clickOnPurpose);
            viewPurpose.append(purpose);
            $('.list-of-purposes_wrap-for-purposes').append(viewPurpose);
        } else {
            Swal.fire({
                text: 'Призначеня платежа з назвою ' + userPurpose + ' вже існує',
                icon: 'error'
            });
        }
    })

}

// Вибір доходів чи витрат

const addMoneyToWallet = $('.add_money_to_wallet');
const removeMoneyToWallet = $('.remove_money_from_wallet');
const formOfMoney = $('.form_add_money');
const addOrRemove = document.querySelector('.addOrRemove');

addMoneyToWallet.on('click', () => {
    addMoneyToWallet.addClass('idColor');
    removeMoneyToWallet.removeClass('idColor');
    formOfMoney.addClass('addFormColor').removeClass('removeFromColor');
    addOrRemove.value = '+';
    countTransactions($('.currentWallet').val(), $('.addOrRemove').val());
});

removeMoneyToWallet.on('click', () => {
    removeMoneyToWallet.addClass('idColor');
    addMoneyToWallet.removeClass('idColor');
    formOfMoney.removeClass('addFormColor').addClass('removeFromColor');
    addOrRemove.value = '-';
    countTransactions($('.currentWallet').val(), $('.addOrRemove').val());
});


$(document).ready(function () {
    countTransactions($('.currentWallet').val(), '+');
});


$('.submit_add_wallet').on('click', addNewWallet);

$('.button_of_transaction').on('click', addWalletTransaction);

$('.list-of-purposes_btn-add-purpose').on('click', addNewPurpose);
$('.list-of-purposes_purpose-name').on('keydown', function (event) {
    if (event.keyCode === 13) {
        addNewPurpose();
    }
});
$('.list-of-purposes_add-purpose').on('click', () => {
    console.log("Good");
    $('.list-of-purposes_purpose-name').toggleClass('list-of-purposes__disable').toggleClass('list-of-purposes__active');
    $('.list-of-purposes_btn-add-purpose').toggleClass('list-of-purposes__disable').toggleClass('list-of-purposes__active');
});





