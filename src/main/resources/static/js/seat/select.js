const params = new URLSearchParams(location.search);
const performanceId = params.get('performanceId');
const isDemo = !performanceId;

const state = {
  seats: [],
  selected: new Set(),
  held: false,
  holdToken: null
};

const holdStorageKey = performanceId ? `seatHold:${performanceId}` : null;

function restoreSavedHold() {
  if (!holdStorageKey) return;

  try {
    const saved = JSON.parse(sessionStorage.getItem(holdStorageKey));
    if (!saved) return;

    const seatIds = saved.performanceSeatIds;
    const expiresAt = Date.parse(saved.holdExpiresAt);

    const usable =
      saved.performanceId === performanceId &&
      typeof saved.holdToken === 'string' &&
      Array.isArray(seatIds) &&
      seatIds.length > 0 &&
      Number.isFinite(expiresAt) &&
      expiresAt > Date.now() &&
      seatIds.every(id => state.seats.some(
        seat => seat.performanceSeatId === id && seat.status === 'HELD'
      ));

    if (!usable) {
      sessionStorage.removeItem(holdStorageKey);
      return;
    }

    state.holdToken = saved.holdToken;
    state.selected = new Set(seatIds);
    state.held = true;
    renderMap();
    message('선점 정보를 불러왔습니다. 주문할 때 서버에서 다시 확인합니다.', 'success');
  } catch {
    // 브라우저 저장소를 사용할 수 없으면 현재 페이지에서만 선점을 유지
  }
}

const $ = id => document.getElementById(id);
const money = value => `${Number(value || 0).toLocaleString('ko-KR')}원`;

const demoSeats = Array.from({ length: 80 }, (_, i) => {
  const row = Math.floor(i / 10);
  const grade = row < 2 ? 'S' : [2, 3, 5].includes(row) ? 'A' : 'R';

  return {
    performanceSeatId: i + 1,
    seatId: i + 1,
    rowLabel: String.fromCharCode(65 + row),
    seatNumber: i % 10 + 1,
    gradeName: grade,
    price: grade === 'S' ? 200000 : grade === 'A' ? 150000 : 130000,
    status: 'AVAILABLE'
  };
});

function gradeClass(name) {
  const grade = String(name || '').trim().toUpperCase().replace(/석$/, '');
  return ['S', 'A', 'R'].includes(grade)
    ? `grade-${grade.toLowerCase()}`
    : '';
}

function gradeLabel(name) {
  const grade = String(name || '').trim();
  return grade.endsWith('석') ? grade : `${grade}석`;
}

function showPerformanceInfo() {
  if (isDemo) {
    $('modeNote').textContent = '화면 미리보기 · 예시 정보';
    $('performanceName').textContent = '뮤지컬 공연 A';
    $('performanceMeta').textContent =
      '2026. 11. 01. 오후 7:30 · 공연장 A · 1층 50석 / 2층 30석';
    $('performanceBadge').textContent = '예시 회차';
    return;
  }

  $('modeNote').textContent = '선택한 공연 회차';
  $('performanceName').textContent =
    params.get('concertName') || `공연 회차 #${performanceId}`;

  $('performanceMeta').textContent = [
    params.get('performanceDate'),
    params.get('venueName'),
    '1층 50석 / 2층 30석'
  ].filter(Boolean).join(' · ');

  $('performanceBadge').textContent = `회차 #${performanceId}`;
}

function message(text, type = '') {
  $('message').textContent = text;
  $('message').className = `message ${type}`;
}

function escapeHtml(value) {
  return String(value ?? '').replace(
    /[&<>"']/g,
    c => ({
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;'
    })[c]
  );
}

function renderSummary() {
  const picked = state.seats.filter(
    seat => state.selected.has(seat.performanceSeatId)
  );

  $('selectedList').innerHTML = picked.length
    ? picked.map(seat => `
        <div class="picked">
          <span>
            <b>${escapeHtml(seat.rowLabel)}열 ${seat.seatNumber}번</b>
            <small>${escapeHtml(gradeLabel(seat.gradeName))}</small>
          </span>
          <strong>${money(seat.price)}</strong>
        </div>
      `).join('')
    : '<p class="empty">아직 선택한 좌석이 없습니다.</p>';

  $('totalPrice').textContent = money(
    picked.reduce((sum, seat) => sum + Number(seat.price || 0), 0)
  );

  $('holdButton').disabled = !picked.length || state.held;
  $('holdButton').textContent = state.held
    ? '선점 완료'
    : `좌석 ${picked.length}개 임시 선점하기`;
	
  $('cancelHoldButton').hidden = !state.held;
  $('refreshButton').disabled = state.held;
}

function renderMap() {
  const map = $('seatMap');
  map.replaceChildren();

  const rows = new Map();

  state.seats.forEach(seat => {
    const key = seat.rowLabel || '?';
    if (!rows.has(key)) rows.set(key, []);
    rows.get(key).push(seat);
  });

  const floors = [
    ['1층', ['A', 'B', 'C', 'D', 'E']],
    ['2층', ['F', 'G', 'H']],
    ['기타', [...rows.keys()].filter(label => !'ABCDEFGH'.includes(label))]
  ];

  for (const [floorName, labels] of floors) {
    const present = labels.filter(label => rows.has(label));
    if (!present.length) continue;

    const floor = document.createElement('div');
    floor.className = 'floor';

    const heading = document.createElement('div');
    heading.className = 'floor-title';
    heading.textContent =
      `${floorName} · ${present.reduce(
        (sum, label) => sum + rows.get(label).length, 0
      )}석`;
    floor.append(heading);

    for (const label of present) {
      const seats = rows.get(label);
      const row = document.createElement('div');
      row.className = 'seat-row';

      const marker = document.createElement('span');
      marker.className = 'row-label';
      marker.textContent = label;
      row.append(marker);

      seats.sort((a, b) => a.seatNumber - b.seatNumber)
        .forEach(seat => {
          const button = document.createElement('button');
          button.type = 'button';
          button.className =
            `seat ${gradeClass(seat.gradeName)} ` +
            `${String(seat.status || '').toLowerCase()}` +
            `${state.selected.has(seat.performanceSeatId) ? ' selected' : ''}`;

          button.textContent = seat.seatNumber;
          button.title =
            `${label}열 ${seat.seatNumber}번 · ${seat.gradeName} · ` +
            `${money(seat.price)} · ${seat.status}`;

          button.setAttribute('aria-label', button.title);
          button.setAttribute(
            'aria-pressed',
            state.selected.has(seat.performanceSeatId)
          );

          button.disabled = seat.status !== 'AVAILABLE' || state.held;

          button.addEventListener('click', () => {
            if (state.selected.has(seat.performanceSeatId)) {
              state.selected.delete(seat.performanceSeatId);
            } else {
              state.selected.add(seat.performanceSeatId);
            }

            renderMap();
            renderSummary();
            message('');
          });

          row.append(button);
        });

      floor.append(row);
    }

    if (floorName === '1층' || floorName === '2층') {
      const access = document.createElement('div');
      access.className = 'floor-access';

      const left = document.createElement('span');
      const right = document.createElement('span');

      if (floorName === '1층') {
        left.textContent = '← 비상구';
        left.className = 'emergency';
        right.textContent = '입구 →';
      } else {
        left.textContent = '← 입구';
        right.textContent = '비상구 →';
        right.className = 'emergency';
      }

      access.append(left, right);
      floor.append(access);
    }

    map.append(floor);
  }

  $('mapDescription').textContent =
    `${state.seats.length}개 좌석 · ` +
    `선택 가능 ${state.seats.filter(s => s.status === 'AVAILABLE').length}개`;

  renderSummary();
}

async function loadSeats() {
if (state.held) {
  message('먼저 임시 선점을 취소해 주세요.', 'error');
  return;
}

message('');
state.selected.clear();
showPerformanceInfo();

if (isDemo) {
    state.seats = demoSeats.map(seat => ({ ...seat }));
    renderMap();
    return;
  }

  try {
    const response = await fetch(
      `/api/performances/${encodeURIComponent(performanceId)}/seats`,
      { credentials: 'same-origin' }
    );

    if (
      !response.ok ||
      response.redirected ||
      !(response.headers.get('content-type') || '').includes('json')
    ) {
      throw new Error(
        response.status === 401 || response.redirected
          ? '로그인 후 다시 열어 주세요.'
          : '좌석 정보를 불러오지 못했습니다.'
      );
    }

    state.seats = await response.json();
    renderMap();
  } catch (error) {
    state.seats = [];
    $('seatMap').textContent = '좌석 정보를 표시할 수 없습니다.';
    $('mapDescription').textContent = '';
    renderSummary();
    message(error.message, 'error');
  }
}

async function holdSeats() {
  if (!state.selected.size || state.held) return;

  if (isDemo) {
    message('미리보기에서는 실제 선점 요청을 보내지 않습니다.', 'success');
    return;
  }

  $('holdButton').disabled = true;
  $('refreshButton').disabled = true;
  message('좌석을 선점하는 중입니다.');

  try {
    const response = await fetch(
      `/api/performances/${encodeURIComponent(performanceId)}/holds`,
      {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          performanceSeatIds: [...state.selected]
        })
      }
    );

    if (!response.ok) {
      throw new Error(
        response.status === 409
          ? '선택한 좌석 중 이미 선점된 좌석이 있습니다. 좌석을 새로고침해 주세요.'
          : '선점에 실패했습니다. 다시 시도해 주세요.'
      );
    }

    const result = await response.json();
	state.holdToken = result.holdToken;
    state.held = true;
	try {
	  sessionStorage.setItem(holdStorageKey, JSON.stringify({
	    performanceId,
	    holdToken: result.holdToken,
	    performanceSeatIds: result.performanceSeatIds,
	    holdExpiresAt: result.holdExpiresAt
	  }));
	} catch {
	  // 저장에 실패해도 현재 페이지의 선점은 유지
	}
    renderMap();

    message(
      `선점 완료 · ${new Date(result.holdExpiresAt)
        .toLocaleTimeString('ko-KR')}까지 유효합니다. ` +
      '결제 화면 연결은 준비 중입니다.',
      'success'
    );
  } catch (error) {
	message(error.message, 'error');
	$('holdButton').disabled = false;
	$('refreshButton').disabled = false;
  }
}

async function cancelHold() {
  if (!state.held || !state.holdToken || isDemo) return;

  $('cancelHoldButton').disabled = true;
  message('임시 선점을 취소하는 중입니다.');

  try {
    const response = await fetch(
      `/api/performances/${encodeURIComponent(performanceId)}/holds/cancel`,
      {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ holdToken: state.holdToken })
      }
    );

	if (response.redirected || response.status !== 204) {
	  throw new Error('선점 취소에 실패했습니다.');
	}

    state.held = false;
    state.holdToken = null;
	try {
	  sessionStorage.removeItem(holdStorageKey);
	} catch {
	  // 저장소를 사용할 수 없는 경우
	}
    await loadSeats();
    message('임시 선점이 취소되었습니다.', 'success');
  } catch (error) {
    message(error.message, 'error');
  } finally {
    $('cancelHoldButton').disabled = false;
  }
}

$('refreshButton').addEventListener('click', loadSeats);
$('holdButton').addEventListener('click', holdSeats);
$('cancelHoldButton').addEventListener('click', cancelHold);
loadSeats().then(() => {
  if (state.seats.length > 0) restoreSavedHold();
});